package nl.focalor.utobot.base.input.listener;

import java.util.Set;
import nl.focalor.utobot.base.input.CommandInput;
import nl.focalor.utobot.base.input.IInput;
import nl.focalor.utobot.base.input.IResult;
import nl.focalor.utobot.base.input.handler.ICommandHandler;
import nl.focalor.utobot.base.input.handler.IInputHandlerFactory;
import nl.focalor.utobot.base.input.handler.IRegexHandler;

public interface IInputListener {

	public IResult onMessage(String source, String message);

	public IResult onNonCommand(IInput input);

	public IResult onCommand(CommandInput command);

	public Set<IInputHandlerFactory> getFactories();

	public Set<IRegexHandler> getRegexHandlers();

	public Set<ICommandHandler> getCommandHandlers();

}
