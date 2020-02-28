package de.MarkusTieger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class Bot extends Plugin {
	
	public static TS3Query query;
	public static TS3Config config;
	
	public String user = "TS3-Server-Bot", pwd = "YtUNqV60";
	
	@Override
	public void onEnable() {
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandLink());
		try {
			config = new TS3Config();
			
			config.setHost("127.0.0.1");
			config.setFloodRate(FloodRate.UNLIMITED);
			config.setReconnectStrategy(ReconnectStrategy.constantBackoff());
			
			query = new TS3Query(config);
			query.connect();
			query.getApi().login(user, pwd);
			query.getApi().selectVirtualServerByPort(9987);
			query.getApi().setNickname("TS3-Bot" + new Random().nextInt());
			int afk = 26;
			int sub1 = 25;
			int sub2 = 30;
			int sub3 = 31;
			int warten = 24;
			ProxyServer.getInstance().getScheduler().schedule(this, new Runnable() {
				
				@Override
				public void run() {
					for(Client c : query.getApi().getClients()) {
						if(c.getIdleTime() >= 5*60*1000) {
							if(c.getChannelId() != afk) {
								query.getApi().moveClient(c.getId(), afk);
							}
						}
						if(c.getChannelId() == warten) {
							if(getGroup(sub1).getTotalClients() == 1) {
								query.getApi().moveClient(c.getId(), sub1);
								continue;
							}
							
							if(getGroup(sub2).getTotalClients() == 1) {
								query.getApi().moveClient(c.getId(), sub2);
								continue;
							}
							
							if(getGroup(sub3).getTotalClients() == 1) {
								query.getApi().moveClient(c.getId(), sub3);
								continue;
							}
						}
						
					}
					boolean open = false;
					if(getGroup(sub1).getTotalClients() >= 1) {
						open = true;
					}
					if(getGroup(sub2).getTotalClients() >= 1) {
						open = true;
					}
					if(getGroup(sub3).getTotalClients() >= 1) {
						open = true;
					}
					int sup = 29;
					try {
						if(open) {
							query.getApi().editChannel(sup, ChannelProperty.CHANNEL_NAME, "Support > Open");
						} else {
							query.getApi().editChannel(sup, ChannelProperty.CHANNEL_NAME, "Support > Closed");
						}
					} catch(Exception e) {
						
					}
					
				}
			}, 30, 30, TimeUnit.SECONDS);
			
		} catch(Exception e) {
			
		}
		
		
	}
	
	public static Channel getGroup(int id) {
		for(Channel channel : query.getApi().getChannels()) {
			if(channel.getId() == id) {
				return channel;
			}
		}
		return null;
	}

}
