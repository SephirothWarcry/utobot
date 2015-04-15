package nl.focalor.utobot.base.service;

import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotService implements IBotService {
	@Autowired
	private PircBotX bot;
	@Autowired
	private List<IMessagingService> messagingServices;

	@Override
	@PostConstruct
	public void startBot() {
		new Thread(() -> {
			try {
				bot.startBot();
			} catch (IOException | IrcException ex) {
				throw new RuntimeException("Failed starting server", ex);
			}
		}).start();
	}

	@Override
	public void broadcast(String message) {
		for (IMessagingService service : messagingServices) {
			service.broadcastMessage(message);
		}
	}

}
