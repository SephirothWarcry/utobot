package nl.focalor.utobot.irc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import nl.focalor.utobot.base.input.CommandInput;
import nl.focalor.utobot.base.input.IResult;
import nl.focalor.utobot.base.input.handler.AbstractCommandHandler;
import nl.focalor.utobot.base.input.listener.InputListener;
import nl.focalor.utobot.irc.input.IrcInputListener;
import nl.focalor.utobot.util.TestBase;

import org.junit.Before;
import org.junit.Test;

public class CommandHandlerListenerTest extends TestBase {
	private SimpleCommandHandlerListener listener;;

	@Before
	public void init() {
		listener = new SimpleCommandHandlerListener();
	}

	@Test
	public void recognizeCommand() throws Exception {
		// Test
		listener.onMessage(buildMessageEvent("!test"));

		// Verify
		CommandInput call = listener.getLastCall();
		assertNotNull(call);
		assertEquals("test", call.getCommand());
	}

	@Test
	public void unknownCommand() throws Exception {
		// Test
		listener.onMessage(buildMessageEvent("!alert"));

		// Verify
		CommandInput call = listener.getLastCall();
		assertNull(call);
	}

	private static class SimpleCommandHandlerListener extends IrcInputListener {
		private static CommandInput lastCall;

		public SimpleCommandHandlerListener() {
			super(new InputListener() {
				{
					setCommandHandlers(Arrays.asList(new AbstractCommandHandler("test") {
						@Override
						public IResult handleCommand(CommandInput event) {
							lastCall = event;
							return null;
						}

						@Override
						public boolean hasHelp() {
							return false;
						}

						@Override
						public List<String> getHelpBody() {
							return null;
						}

						@Override
						public String getSimpleHelp() {
							return null;
						}
					}));
				};
			});
		}

		public CommandInput getLastCall() {
			return lastCall;
		}

	}
}
