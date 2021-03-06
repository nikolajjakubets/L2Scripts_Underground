package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Yorie
 */
// TODO: General, this quest should be checked for all races. Now it's checked only for elves
public class _10325_SearchingForNewPower extends Quest implements ScriptFile
{
	private final static int GALLINT = 32980;
	/* Race depended talk NPCs */
	private final static int TALBOT = 32156;	// Human
	private final static int CINDET = 32148;	// Elves
	private final static int BLACK = 32161;		// Dark Elves
	private final static int HERZ = 32151;		// Orcs
	private final static int KINCAID = 32159;	// Dwarves
	private final static int XONIA = 32144;		// Kamael
	/*~Race dependent talk NPCs~*/
	
	private final static int SPIRITSHOT = 2509;
	private final static int SOULSHOT = 1835;
	
	private boolean talkerAdded;
	
	public _10325_SearchingForNewPower()
	{
		super(false);
		
		talkerAdded = false;
		addStartNpc(GALLINT);
		addTalkId(GALLINT);
		addTalkId(BLACK);
		addTalkId(KINCAID);
		addTalkId(CINDET);
		addTalkId(TALBOT);
		addTalkId(HERZ);
		addTalkId(XONIA);
		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10324_FindingMagisterGallint.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "gallint_q10325_3.htm";
			Race race = qs.getPlayer().getRace();
			if(race == Race.DARKELF)
			{
				htmltext = "gallint_q10325_3_darkelves.htm";
				qs.setCond(4);
			}
			else if(race == Race.DWARF)
			{
				htmltext = "gallint_q10325_3_dwarves.htm";
				qs.setCond(6);
			}
			else if(race == Race.ELF)
			{
				htmltext = "gallint_q10325_3_elves.htm";
				qs.setCond(3);
			}
			else if(race == Race.HUMAN)
			{
				htmltext = "gallint_q10325_3_human.htm";
				qs.setCond(2);
			}
			else if(race == Race.KAMAEL)
			{
				htmltext = "gallint_q10325_3_kamael.htm";
				qs.setCond(7);
			}
			else if(race == Race.ORC)
			{
				htmltext = "gallint_q10325_3_orcs.htm";
				qs.setCond(5);
			}
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		final Race race = st.getPlayer().getRace();
		
		switch(npcId)
		{
		case GALLINT:
			if (st.isCompleted())
				htmltext = "gallint_q10325_0.htm";

			else if (cond == 0)
			{
				if (checkStartCondition(st.getPlayer()))
					htmltext = "gallint_q10325_1.htm";
				else
					htmltext = "gallint_q10325_5.htm";
			}
			else if(cond >= 8)
			{
				htmltext = "gallint_q10325_4.htm";
				st.giveItems(ADENA_ID, 12000);
				st.getPlayer().addExpAndSp(3254, 2400);
				st.playSound(SOUND_FINISH);
				if(st.getPlayer().isMageClass())
					st.giveItems(SPIRITSHOT, 1000);
				else
					st.giveItems(SOULSHOT, 1000);
				st.exitCurrentQuest(false);
			}
			else if(cond > 0)
				htmltext = "gallint_q10325_taken.htm";
			break;
		case TALBOT:
			if(race == Race.HUMAN)
			{
				if(cond == 2)
				{
					htmltext = "talbot_q10325_1.htm";
					st.setCond(st.getCond() + 6);
				}
				else
					htmltext = "talbot_q10325_taken.htm";
			}
			break;
		case CINDET:
			if(race == Race.ELF)
			{
				if(cond == 3)
				{
					htmltext = "cindet_q10325_1.htm";
					st.setCond(st.getCond() + 6);
				}
				else
					htmltext = "cindet_q10325_taken.htm";
			}
			break;
		case BLACK:
			if(race == Race.DARKELF)
			{
				if(cond == 4)
				{
					htmltext = "black_q10325_1.htm";
					st.setCond(st.getCond() + 6);
				}
				else
					htmltext = "black_q10325_taken.htm";
			}
			break;
		case HERZ:
			if(race == Race.ORC)
			{
				if(cond == 5)
				{
					htmltext = "herz_q10325_1.htm";
					st.setCond(st.getCond() + 6);
				}
				else
					htmltext = "herz_q10325_taken.htm";
			}
			break;
		case KINCAID:
			if(race == Race.DWARF)
			{
				if(cond == 6)
				{
					htmltext = "kincaid_q10325_1.htm";
					st.setCond(st.getCond() + 6);
				}
				else
					htmltext = "kincaid_q10325_taken.htm";
			}
			break;
		case XONIA:
			if(race == Race.KAMAEL)
			{
				if(cond == 7)
				{
					htmltext = "xonia_q10325_1.htm";
					st.setCond(st.getCond() + 6);
				}
				else
					htmltext = "xonia_q10325_taken.htm";
			}
			break;
		}
		return htmltext;
	}
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}