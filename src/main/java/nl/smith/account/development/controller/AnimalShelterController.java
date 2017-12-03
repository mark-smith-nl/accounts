package nl.smith.account.development.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
@RequestMapping(AnimalShelterController.CONTROLLER_MAPPING)
public class AnimalShelterController {

	public static final String CONTROLLER_MAPPING = "animalShelter";

	public static final String EDIT_MAPPING = "/edit";

	public static final String SAVE_MAPPING = "/save";

	@InitBinder("animalShelter")
	public void initBinder(WebDataBinder webDataBinder, HttpServletRequest httpServletRequest) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Pattern pattern = Pattern.compile("animals\\[(\\d+)]\\.className");
		AnimalShelter animalShelter = (AnimalShelter) webDataBinder.getTarget();
		List<Animal> animals = animalShelter.getAnimals();

		Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();

			Matcher matcher = pattern.matcher(parameterName);
			if (matcher.matches()) {
				Integer index = Integer.parseInt(matcher.group(1));
				String className = httpServletRequest.getParameter(parameterName);
				Class<?> clazz = Class.forName(className);
				if (Animal.class.isAssignableFrom(clazz)) {
					Constructor<?> constructor = clazz.getConstructor();
					if (constructor != null) {
						animals.add(index, (Animal) constructor.newInstance());
					}
				}
			}
		}
	}

	@GetMapping(EDIT_MAPPING)
	public @ResponseBody String edit(@RequestParam(defaultValue = "post") String method) {
		AnimalShelter animalShelter = new AnimalShelter("Animal Shelter");
		List<Animal> animals = new ArrayList<>();
		animals.add(new Cat("Felix"));
		animals.add(new Dog("Bonzo"));
		animals.add(new Dog("Loud Bonzo", true));
		animals.add(new Cat("Topcat"));
		animalShelter.setAnimals(animals);

		List<String> html = new ArrayList<>();
		html.add("<html>");
		html.add("<head>");
		html.add("<title>Edit</title>");
		html.add("</head>");
		html.add("<body>");
		html.add("<form action=\"/" + CONTROLLER_MAPPING + SAVE_MAPPING + "\" method=\"" + method + "\">");
		html.add("<input type=\"text\" name=\"name\" value=\"" + animalShelter.getName() + "\"><br>");
		html.add("<br>");

		for (int i = 0; i < animals.size(); i++) {
			Animal animal = animals.get(i);
			html.add("<input type=\"text\" name=\"animals[" + i + "].className\" value=\"" + animal.getClass().getCanonicalName() + "\"><br>");

			html.add("<input type=\"text\" name=\"animals[" + i + "].name\" value=\"" + animal.getName() + "\"><br>");
			if (animal instanceof Dog) {
				Dog dog = (Dog) animal;
				html.add("<input type=\"text\" name=\"animals[" + i + "].barks\" value=\"" + dog.isBarks() + "\"><br>");
			}

		}
		html.add("<input type=\"submit\" value=\"Opslaan\">\n");
		html.add("</form>");
		html.add("</body>");
		html.add("</html>");

		return String.join("\n", html);
	}

	@RequestMapping(value = SAVE_MAPPING, method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody String save(AnimalShelter animalShelter) {
		StringBuilder result = new StringBuilder("Result: " + animalShelter.getName());
		animalShelter.getAnimals().forEach(animal -> result.append("<br> -> " + animal.getClass().getCanonicalName() + " " + animal.getName()));
		result.append("<br><a href=\"http://localhost:8080/" + CONTROLLER_MAPPING + EDIT_MAPPING + "\">Edit (Post)</a>");
		result.append("<br><a href=\"http://localhost:8080/" + CONTROLLER_MAPPING + EDIT_MAPPING + "?method=get\">Edit (Get)</a>");
		return result.toString();
	}

}
