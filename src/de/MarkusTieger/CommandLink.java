package de.MarkusTieger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandLink extends Command {
	

	public CommandLink() {
		super("link");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		try {
			if(sender instanceof ProxiedPlayer) {
				String ip = ((ProxiedPlayer) sender).getAddress().getAddress().getHostAddress();
				String group = BungeePexBridge.getPerms().getPlayerGroups((ProxiedPlayer) sender).get(0);
				ServerGroup g = null;
				for(ServerGroup groupTS : Bot.query.getApi().getServerGroups()) {
					if(groupTS.getName().equalsIgnoreCase(group)) {
						g = groupTS;
					}
				}
				for(Client c : Bot.query.getApi().getClients()) {
					if(c.getIp().equalsIgnoreCase(ip)) {
						List<ServerGroup> groupC = getGroups(c);
						if(!c.isInServerGroup(g.getId())) {
							Bot.query.getApi().addClientToServerGroup(g.getId(), c.getDatabaseId());
							for(ServerGroup g1 : groupC) {
								Bot.query.getApi().removeClientFromServerGroup(g1.getId(), c.getDatabaseId());
							}
						}
						sender.sendMessage("Succsessfully Linked with " + c.getNickname());
						Bot.query.getApi().sendTextMessage(TextMessageTargetMode.SERVER, c.getId(), "You are now linked with " + sender.getName());
						try {
							URL url = new URL("https://minotar.net/avatar/" + sender.getName() + "/16.png");
							InputStream in = new BufferedInputStream(url.openStream());
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							byte[] buf = new byte[1024];
							int i = 0;
							while(-1 != (i = in.read(buf))) {
								out.write(buf, 0, i);
							}
							in.close();
							out.close();
							byte[] response = out.toByteArray();
							long iconid = Bot.query.getApi().uploadIconDirect(response);
							Bot.query.getApi().addClientPermission(c.getDatabaseId(), "i_icon_id", (int)iconid, false);
						} catch(Exception ex) {
							ex.printStackTrace();
						}
						Bot.query.getAsyncApi().editClient(c.getId(), ClientProperty.CLIENT_DESCRIPTION, "Minecraft: " + sender.getName());
					}
				}
			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private List<ServerGroup> getGroups(Client c) {
		ArrayList<ServerGroup> groups = new ArrayList<ServerGroup>();
		for(ServerGroup g : Bot.query.getApi().getServerGroupsByClientId(c.getDatabaseId())) {
			groups.add(g);
		}
		return groups;
	}

}
