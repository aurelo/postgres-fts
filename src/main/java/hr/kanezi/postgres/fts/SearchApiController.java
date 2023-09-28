package hr.kanezi.postgres.fts;

import lombok.Data;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchApiController {

    record Result(String title, String description, String url){}

    @Data
    public static class Results {
        List<Result> results = new ArrayList<>();

        public static Results from(String q){
            Results res = new Results();
            res.results.add(new Result(q, q, q));
            res.results.add(new Result("<span class='ui red text'>" + q + "</span>", q + " <span class='ui large purple text'>" + q + "</span>" + " - " + q, q));

            return res;
        }
    }


    @GetMapping
    public Results search(@RequestParam(required = false) String q) {
        return Results.from(q);
    }

    @GetMapping("/fuzzy")
    public Results fuzzy(@RequestParam(required = false) String q) {
        return Results.from(q);
    }

}
