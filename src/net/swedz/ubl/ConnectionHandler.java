package net.swedz.ubl;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class ConnectionHandler implements Listener {
	private void checkFor(final ProxiedPlayer player) {
		ProxyServer.getInstance().getScheduler().schedule(Main.instance(), new Runnable() {
			public void run() {
				String uuid = player.getUniqueId().toString();
				String serverName = player.getServer().getInfo().getName();
				
				Configuration config = Main.getConfig();
				if(config.getList("servers").contains(serverName) && Bans.updating) {
					player.disconnect(
							"§4BungeeUBL is being updated."
							+ "\n§7Please be patient.");
					return;
				}
				
				if(config.getList("servers").contains(serverName) && Bans.list.containsKey(uuid)) {
					player.disconnect(
							"§4You are on the UBL!"
							+ "\n"
							+ "\n§cReason: §f" + Bans.list.get(uuid)
							+ "\n"
							+ "\n§7Read more here: §nreddit.com/r/uhccourtroom/wiki");
					return;
				}
			}
		}, 1, TimeUnit.SECONDS);
	}
	
	@EventHandler
	public void onPostLogin(ServerConnectedEvent e) {
		ProxiedPlayer player = e.getPlayer();
		checkFor(player);
	}
}
