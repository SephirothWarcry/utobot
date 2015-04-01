package nl.focalor.utobot.base.model.service;

import java.util.ArrayList;
import java.util.List;

import nl.focalor.utobot.base.model.Person;
import nl.focalor.utobot.base.model.dao.IPersonDao;
import nl.focalor.utobot.utopia.model.Province;
import nl.focalor.utobot.utopia.service.IProvinceService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService implements IPersonService {
	@Autowired
	private IPersonDao personDao;
	@Autowired
	private IProvinceService provinceService;

	@Override
	@Transactional
	public void create(Person person) {
		personDao.create(person);
	}

	@Override
	@Transactional(readOnly = true)
	public Person find(String name) {
		List<Person> people = new ArrayList<>();
		people.addAll(personDao.find(name));

		// Check for common postfixes
		int index = name.indexOf('|');
		if (index > 0) {
			people.addAll(personDao.find(name.substring(0, index)));
		}

		if (people.isEmpty()) {
			return null;
		} else if (people.size() == 1) {
			return people.get(0);
		} else {
			throw new RuntimeException("Multiple people found for name " + name);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<Person> loadPeople(String namePart, String provinceNamePart) {
		List<Person> people = new ArrayList<Person>();
		if (!StringUtils.isEmpty(namePart)) {
			people.addAll(loadPeopleByName(namePart));
		}
		if (!StringUtils.isEmpty(provinceNamePart)) {
			people.addAll(loadPeopleByProvinceName(provinceNamePart));
		}

		List<Person> result = new ArrayList<Person>();
		for (Person person : people) {
			if (!contains(result, person)) {
				result.add(person);
			}
		}
		return result;
	}

	private boolean contains(List<Person> people, Person person) {
		for (Person p : people) {
			if (p.getId() == person.getId()) {
				return true;
			}
		}
		return false;
	}

	private List<Person> loadPeopleByName(String namePart) {
		List<Person> result = personDao.find(namePart);
		for (Person person : result) {
			loadPerson(person);
		}
		return result;
	}

	private List<Person> loadPeopleByProvinceName(String provinceNamePart) {
		List<Person> result = new ArrayList<Person>();
		List<Province> provinces = provinceService.find(null, provinceNamePart);

		for (Province prov : provinces) {
			Person person = personDao.get(prov.getPersonId());
			person.setProvince(prov);
			result.add(person);
		}
		return result;
	}

	private void loadPerson(Person person) {
		List<Province> provinces = provinceService.find(person.getId(), null);
		if (provinces.size() == 1) {
			person.setProvince(provinces.get(0));
		}
	}
}