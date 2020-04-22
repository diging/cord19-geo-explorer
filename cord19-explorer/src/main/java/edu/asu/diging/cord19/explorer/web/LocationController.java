package edu.asu.diging.cord19.explorer.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class LocationController {

	@Autowired
	private PublicationRepository pubRepo;

	@RequestMapping("/location/{location}")
	public String findPublications(@PathVariable("location") String location, Model model) {
		List<PublicationImpl> pubs = pubRepo.findByBodyTextLocationMatchesLocationName(location);
		for (PublicationImpl pub : pubs) {
			pub.setBodyText(pub.getBodyText().stream()
					.filter(p -> p.getLocationMatches().stream().anyMatch(m -> m.getLocationName().equals(location)))
					.collect(Collectors.toList()));
		}
		model.addAttribute("publications", pubs);
		model.addAttribute("location", location);
		return "location";
	}
}
