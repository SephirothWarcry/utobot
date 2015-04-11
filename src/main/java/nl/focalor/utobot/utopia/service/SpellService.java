package nl.focalor.utobot.utopia.service;

import nl.focalor.utobot.base.jobs.IJobsService;
import nl.focalor.utobot.base.model.entity.Person;
import nl.focalor.utobot.base.model.service.IPersonService;
import nl.focalor.utobot.base.service.IBotService;
import nl.focalor.utobot.utopia.job.SpellCastCompletedJob;
import nl.focalor.utobot.utopia.model.SpellType;
import nl.focalor.utobot.utopia.model.UtopiaSettings;
import nl.focalor.utobot.utopia.model.entity.SpellCast;
import nl.focalor.utobot.utopia.model.repository.SpellCastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpellService implements ISpellService {
	@Autowired
	private SpellCastRepository spellCastDao;
	@Autowired
	private IJobsService jobsService;
	@Autowired
	private IBotService botService;
	@Autowired
	private IPersonService personService;

	private final Map<String, SpellType> knownSpells;

	@Autowired
	public SpellService(UtopiaSettings settings) {
		super();

		knownSpells = new HashMap<>();
		for (SpellType spell : settings.getSpells()) {
			knownSpells.put(spell.getId(), spell);
		}
	}

	@Override
	public Collection<SpellType> getKnownSpellTypes() {
		return knownSpells.values();
	}

	@Override
	@Transactional
	public void create(SpellCast cast, boolean persist) {
		if (persist) {
			spellCastDao.save(cast);
		}
		jobsService
				.scheduleAction(new SpellCastCompletedJob(botService, this, personService, cast), cast.getLastHour());
	}

	@Override
	public void delete(SpellCast spellCast) {
		spellCastDao.delete(spellCast);
	}

	@Override
	public List<SpellCast> findAll() {
		return (List<SpellCast>) spellCastDao.findAll();
	}

	@Override
	public List<SpellCast> findByPerson(Person person) {
		return spellCastDao.findByPerson(person);
	}
}
