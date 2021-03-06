package nl.focalor.utobot.utopia.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.focalor.utobot.base.input.CommandInput;
import nl.focalor.utobot.base.input.ErrorResult;
import nl.focalor.utobot.base.input.IResult;
import nl.focalor.utobot.base.input.ReplyResult;
import nl.focalor.utobot.base.input.handler.AbstractGenericCommandHandler;
import nl.focalor.utobot.base.model.entity.Person;
import nl.focalor.utobot.base.model.service.IPersonService;
import nl.focalor.utobot.utopia.model.Personality;
import nl.focalor.utobot.utopia.model.Race;
import nl.focalor.utobot.utopia.model.entity.Province;
import nl.focalor.utobot.utopia.service.IProvinceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddProvHandler extends AbstractGenericCommandHandler {
	public static final String COMMAND_NAME = "addprov";
	private static final String[] NICKS = { "provadd" };
	private static final Pattern pattern = Pattern.compile("(.*) - (.*) \\[(.*)/(.*)\\]");

	@Autowired
	private IPersonService personService;
	@Autowired
	private IProvinceService provinceService;

	public AddProvHandler() {
		super(COMMAND_NAME, NICKS);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public IResult handleCommand(CommandInput event) {
		Matcher matcher = pattern.matcher(event.getArgument());
		if (matcher.find()) {
			return createProvince(matcher);
		} else {
			return new ErrorResult("Province could not added, check syntax: PLAYER - PROVINCE [RACE/PERSONALITY]");
		}
	}

	private IResult createProvince(Matcher matches) {
		String player = matches.group(1);
		String prov = matches.group(2);
		Race race = Race.parse(matches.group(3));
		Personality personality = Personality.parse(matches.group(4));

		if (personService.find(player, true) != null) {
			return new ErrorResult("Failed adding player, " + player + " already known");
		}
		if (race == Race.UNKNOWN || personality == Personality.UNKNOWN) {
			return new ErrorResult("race (" + matches.group(3) + ") or personality (" + matches.group(4) + ") invalid");
		}

		Person person = new Person();
		person.setName(player);

		Province province = new Province();
		province.setName(prov);
		province.setOwner(person);
		province.setRace(race);
		province.setPersonality(personality);
		provinceService.create(province);

		person.setProvince(province);
		personService.save(person);

		return new ReplyResult("Province added");
	}

	@Override
	public String getSimpleHelp() {
		return "Registers a province with the bot. Use '!help addprov' for more info.";
	}

	@Override
	public List<String> getHelpBody() {
		List<String> helpBody = new ArrayList<String>();
		helpBody.add("Registers a province with the bot.");
		helpBody.add("USAGE:");
		helpBody.add("!addprov <Nick> - <Province> [<Race>/<Profession>]");
		helpBody.add("e.g.:");
		helpBody.add("!addprov Sephi Naughty Nisall [Human/Cleric]");
		return helpBody;
	}
}
