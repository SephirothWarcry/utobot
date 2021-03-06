package nl.focalor.utobot.utopia.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nl.focalor.utobot.base.input.CommandInput;
import nl.focalor.utobot.base.input.IResult;
import nl.focalor.utobot.base.input.MultiReplyResult;
import nl.focalor.utobot.base.input.ReplyResult;
import nl.focalor.utobot.base.input.handler.AbstractGenericCommandHandler;
import nl.focalor.utobot.base.model.service.IPersonService;
import nl.focalor.utobot.utopia.model.entity.Attack;
import nl.focalor.utobot.utopia.service.IAttackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author focalor
 */
@Component
public class ArmiesHandler extends AbstractGenericCommandHandler {
	public static final String COMMAND_NAME = "armies";

	@Autowired
	private IAttackService attackService;
	@Autowired
	private IPersonService personService;

	public ArmiesHandler() {
		super(COMMAND_NAME);
	}

	@Override
	public IResult handleCommand(CommandInput event) {
		List<Attack> attacks = attackService.findAll();
		if (attacks.isEmpty()) {
			return new ReplyResult("No armies found");
		}
		//@formatter:off
		List<String> msgs = attacks.stream()
				.sorted((left, right) -> left.getReturnDate().compareTo(right.getReturnDate()))
				.map(attack -> attack.toString())
				.collect(Collectors.toList());
		//@formatter:on!

		return new MultiReplyResult(msgs);
	}

	@Override
	public String getSimpleHelp() {
		return "Displays all armies out in the KD. Use '!help armies' for more info.";
	}

	@Override
	public List<String> getHelpBody() {
		List<String> helpBody = new ArrayList<String>();
		helpBody.add("Displays all armies out in the KD.");
		helpBody.add("USAGE:");
		helpBody.add("!armies");
		return helpBody;
	}
}
