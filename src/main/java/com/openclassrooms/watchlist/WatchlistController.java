package com.openclassrooms.watchlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WatchlistController {

	private List<WatchlistItem> watchlistItems = new ArrayList<WatchlistItem>();


	@PostMapping("/watchlistItemForm")
	public ModelAndView submitWatchlistItemForm(@Valid WatchlistItem watchlistItem, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return new ModelAndView("watchlistItemForm");
		}

		WatchlistItem existingItem = findWatchlistItemById(watchlistItem.getId());

		if (existingItem.getTitle()==null) {
			if (itemAlreadyExists(watchlistItem.getTitle())) {
				bindingResult.rejectValue("title", "", "This movie is already on your watchlist");
	            return new ModelAndView("watchlistItemForm");
			}
			
			watchlistItem.setId(watchlistItem.nextId());
			watchlistItems.add(watchlistItem);
		} else {
			existingItem.setComment(watchlistItem.getComment());
			existingItem.setPriority(watchlistItem.getPriority());
			existingItem.setRating(watchlistItem.getRating());
			existingItem.setTitle(watchlistItem.getTitle());  
		}


		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("/watchlist");

		return new ModelAndView(redirectView);
	}



	@GetMapping("/watchlistItemForm")
	public ModelAndView showWatchlistItemForm(@RequestParam(required=false) Integer id) {

		String viewName = "watchlistItemForm";

		Map<String,Object> model = new HashMap<String,Object>();

		WatchlistItem watchlistItem = findWatchlistItemById(id);


		model.put("watchlistItem", watchlistItem);

		return new ModelAndView(viewName , model);

	}

	private WatchlistItem findWatchlistItemById(Integer id) {
		for (WatchlistItem watchlistItem : watchlistItems) {
			if (watchlistItem.getId().equals(id)) {
				return watchlistItem;
			} 
		}
		return new WatchlistItem();
	}

	private boolean itemAlreadyExists(String title) {

		for (WatchlistItem watchlistItem : watchlistItems) {
			if (watchlistItem.getTitle().equals(title)) {
				return true;
			}
		}
		return false;
	}

	@GetMapping("/watchlist")
	public ModelAndView getWatchList() {


		String viewName="watchlist";

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("watchlistItems", watchlistItems);
		model.put("numberOfMovies", watchlistItems.size());
		return new ModelAndView(viewName, model);
	}
}
