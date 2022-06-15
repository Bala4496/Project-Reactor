package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.MovieInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.project_reactor.util.CommonUtil;

import java.time.LocalDate;
import java.util.List;

public class MovieInfoService {

    public WebClient webClient;

    public MovieInfoService() {}

    public MovieInfoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public  Flux<MovieInfo> retrieveAllMoviesInfoRestClient(){

        return webClient.get().uri("/v1/movie_infos")
            .retrieve()
            .bodyToFlux(MovieInfo.class)
            .log();
    }

    public  Mono<MovieInfo> retrieveMoviesByIdRestClient(long id){

        return webClient.get().uri("/v1/movie_infos/{id}", id)
            .retrieve()
            .bodyToMono(MovieInfo.class)
            .log();
    }

    public  Flux<MovieInfo> retrieveMoviesFlux(){

        List<MovieInfo> movieInfoList = List.of(new MovieInfo(1l,100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(2l, 101l,"The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo(3l, 102l,"Dark Knight Rises", 2008, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        return Flux.fromIterable(movieInfoList);
    }

    public  Mono<MovieInfo> retrieveMovieInfoMonoUsingId(long movieId){

        var movie = new MovieInfo(movieId, 100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        return Mono.just(movie);
    }

    public  List<MovieInfo> movieList(){
        CommonUtil.delay(1000);
        var moviesList = List.of(new MovieInfo(1l,100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(2l, 101l,"The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo(3l, 102l,"Dark Knight Rises", 2008, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        return moviesList;
    }

    public  MovieInfo retrieveMovieUsingId(long movieId){
        CommonUtil.delay(1000);
        var movie = new MovieInfo(movieId, 100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        return movie;
    }

}
