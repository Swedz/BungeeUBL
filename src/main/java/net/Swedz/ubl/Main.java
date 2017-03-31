package net.Swedz.ubl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
	private static Main instance;
	public static Main instance() {
		return instance;
	}
	
	public static File getConfigurationFile() {
		return new File(Main.instance().getDataFolder(), "config.yml");
	}
	
	private static Configuration config;
	public static Configuration getConfig() {
		return config;
	}
	
	public static void reloadConfig() {
		try {
			if(!Main.instance().getDataFolder().exists())
				Main.instance().getDataFolder().mkdir();
			File configuration = Main.getConfigurationFile();
			if(!configuration.exists()) {
				try {
					configuration.createNewFile();
					try (InputStream in = Main.instance().getResourceAsStream("config.yml")) {
						OutputStream out = new FileOutputStream(configuration);
						ByteStreams.copy(in, out);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(Main.getConfigurationFile());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void saveConfig(Configuration config) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, Main.getConfigurationFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnable() {
		instance = this;
		reloadConfig();
		
		this.addCommand(new UBL());
		new Listeners(this, new ConnectionHandler());
		
		new Bans();
	}
	
	private void addCommand(Command command) {
		this.getProxy().getPluginManager().registerCommand(this, command);
	}
	
	private static class Listeners {
		public Listeners(Plugin instance, Listener...listeners) {
			for(Listener ll : listeners)
				instance.getProxy().getPluginManager().registerListener(instance, ll);
		}
	}
}
