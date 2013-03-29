package com.github.peter200lx.bukkit3390;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Bukkit3390 extends JavaPlugin {
	private static final Logger LOG = Logger.getLogger("Minecraft");

	private static final String MODNAME = "BUKKIT3390";

	protected boolean debug = true;

	@Override
	public void onDisable() {
		// Nothing to do
	}

	@Override
	public void onEnable() {
		if (loadConf()) {
			// Register our events
			getServer().getPluginManager().registerEvents(new ToolListener(), this);

			// Print Plugin loaded message
			if (debug) {
				final PluginDescriptionFile pdfFile = this.getDescription();
				LOG.info("[" + MODNAME + "] version " + pdfFile.getVersion()
						+ " is now loaded with debug enabled");
			}
		} else {
			LOG.warning("[" + MODNAME + "] had an error loading config.yml and is now disabled");
			this.setEnabled(false);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		// Safety check for determining console status
		boolean console = true;
		if (sender instanceof Player) {
			console = false;
		}

		if (cmd.getName().equalsIgnoreCase("bk3390") && (args.length == 1)) {
			if (args[0].contentEquals("reload")) {
				if (sender.isOp()) {
					sender.sendMessage("This currently does nothing");
				} else {
					sender.sendMessage("You don't have permission to reload  the config file");
				}
				return true;
			}
		}
		return false;
	}

	private boolean loadConf() {
		return true; //Currently nothing to load
	}

	/**
	 * Event Listener class for hooking into gameplay.
	 */
	private class ToolListener implements Listener {

		/**
		 * Catch valid mouse clicks on blocks, with further action if block is TRAP_DOOR.
		 *
		 * @param event event when an Action is performed by a user
		 */
		@EventHandler
		public void catchInteract(PlayerInteractEvent event) {
			final Player subject = event.getPlayer();
			Action act = event.getAction();

			if ((act.equals(Action.LEFT_CLICK_BLOCK) || act.equals(Action.RIGHT_CLICK_BLOCK))
					&& subject.getItemInHand().getType().equals(Material.ARROW)) {
				event.setCancelled(true);
				Block clicked = event.getClickedBlock();

				if(clicked.getType().equals(Material.TRAP_DOOR)) {
					MaterialData b = clicked.getState().getData();

					switch(act) {
					case LEFT_CLICK_BLOCK:
						//display block info
						subject.sendMessage("Clicked on TRAP_DOOR:" + data2Str(b));
						break;
					case RIGHT_CLICK_BLOCK:
						//flip block
						Boolean inverted = ((b.getData() & 0x08) == 0x08);
						inverted = !inverted;
						if(inverted) {
							clicked.setData((byte)(clicked.getData()|0x08), false);
						} else {
							clicked.setData((byte)(clicked.getData()&(~0x08)), false);
						}
						subject.sendMessage("The TRAP_DOOR has been flipped!");
						break;
					default:
						subject.sendMessage("Hit unexpected case in " + MODNAME);
						break;
					}
				}
			}
		}

		/**
		 * Convert Material's data value to user-friendly String.
		 *
		 * (Function has been stripped down to only handle TRAP_DOOR)
		 *
		 * @param b object containing a block's Material type and data value
		 * @return user-friendly String representing blocks data value
		 */
		protected String data2Str(MaterialData b) {
			byte data = b.getData();
			switch (b.getItemType()) {
			case TRAP_DOOR:
				return ((TrapDoor) b).getAttachedFace().toString()
						+ (((data & 0x08) == 0x08) ? " INVERTED" : "") + " is "
						+ (((TrapDoor) b).isOpen() ? "OPEN" : "CLOSED");
			default:
				return "" + data;
			}
		}
	}
}
