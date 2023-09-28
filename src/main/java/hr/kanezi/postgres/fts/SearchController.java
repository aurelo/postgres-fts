package hr.kanezi.postgres.fts;

import hr.kanezi.postgres.fts.quotes.QuotesService;
import lombok.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@Value
public class SearchController {

    QuotesService quotesService;

    @GetMapping
    public String home() {
        return "search";
    }

    @GetMapping("/quotes")
    public String quotes(Model model) {
        model.addAttribute("quotes", quotesService.getQuotes());
        return "quotes";
    }

    @GetMapping("/quotes/find")
    public String queryQuotes(@RequestParam(name = "q") String q, Model model) {
        model.addAttribute("quotes", quotesService.findQuotes(q));
        return "quotes";
    }

    @GetMapping("/quotes/fts")
    public String ftsQuotes(@RequestParam(name = "q") String q, Model model) {
        model.addAttribute("quotes", quotesService.ftsFindQuotes(q));
        return "quotes";
    }
}
