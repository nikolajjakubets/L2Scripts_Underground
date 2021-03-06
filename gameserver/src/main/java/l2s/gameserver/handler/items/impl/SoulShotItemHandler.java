package l2s.gameserver.handler.items.impl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.WeaponTemplate;

public class SoulShotItemHandler extends DefaultItemHandler
{
	private static final TIntObjectMap<Skill> SHOT_SKILLS = new TIntObjectHashMap<Skill>();
	static
	{
		SHOT_SKILLS.put(0, SkillHolder.getInstance().getSkill(2039, 1)); // None Grade
		SHOT_SKILLS.put(1, SkillHolder.getInstance().getSkill(2150, 1)); // D Grade
		SHOT_SKILLS.put(2, SkillHolder.getInstance().getSkill(2151, 1)); // C Grade
		SHOT_SKILLS.put(3, SkillHolder.getInstance().getSkill(2152, 1)); // B Grade
		SHOT_SKILLS.put(4, SkillHolder.getInstance().getSkill(2153, 1)); // A Grade
		SHOT_SKILLS.put(5, SkillHolder.getInstance().getSkill(2154, 1)); // S Grade
		SHOT_SKILLS.put(6, SkillHolder.getInstance().getSkill(9193, 1)); // R Grade
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		// soulshot is already active
		if(player.getChargedSoulshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.isFake() || player.getAutoSoulShot().contains(shotId))
			isAutoSoulShot = true;

		if(player.getActiveWeaponInstance() == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		int ssConsumption = weaponItem.getSoulShotCount();
		if(ssConsumption <= 0)
		{
			// Can't use soulshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		int[] reducedSoulshot = weaponItem.getReducedSoulshot();
		if(reducedSoulshot[0] > 0 && Rnd.chance(reducedSoulshot[0]))
			ssConsumption = reducedSoulshot[1];

		if(ssConsumption <= 0)
			return false;

		int grade = weaponItem.getGrade().extOrdinal();
		if(grade != item.getGrade().extOrdinal())
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
			return false;
		}

		if(!player.isFake() && !player.getInventory().destroyItem(item, ssConsumption))
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
			return false;
		}

		if(grade != ItemGrade.NONE.ordinal())
			WorldStatisticsManager.getInstance().updateStat(player, CategoryType.SS_CONSUMED, weaponItem.getGrade().extOrdinal(), ssConsumption);

		Skill skill = player.getAdditionalSSEffect(false, false);
		if(skill == null)
			skill = item.getTemplate().getFirstSkill();
		if(skill == null)
			skill = SHOT_SKILLS.get(grade);

		player.forceUseSkill(skill, player);
		return true;
	}
}