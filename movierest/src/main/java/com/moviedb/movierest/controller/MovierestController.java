package com.moviedb.movierest.controller;

import org.springframework.http.HttpHeaders;

import java.sql.SQLException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moviedb.movierest.model.User_movie;
import com.moviedb.movierest.query.SQLQueries;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequestMapping(value = "api")
public class MovierestController {



    public ResponseEntity<String> initializer(String url){
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5M2ViNTU5YTU4NmM5NDJkNGZmMDAzNzI2NjBhZDg0NyIsInN1YiI6IjYzMTVhZGM1MTUxMWFhMDA3YmE0NzFkMSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.6dr75ZUIUKVx3KLeUfS1dIuL7zgkFYISrjPUkV0HNOk");
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,request,String.class);

        return responseEntity;
    }

    @GetMapping("movie/toprated")
    public ResponseEntity<String> getTopRated() {
        String urlString="https://api.themoviedb.org/3/movie/top_rated?language=es-ES";
        
        return initializer(urlString);
    }

    @GetMapping("genre/movie/list")
    public ResponseEntity<String> getGenreList() {
        String urlString="https://api.themoviedb.org/3/genre/movie/list?language=es-ES";

        return initializer(urlString);
    }

    @GetMapping("movie/popular")
    public ResponseEntity<String> getPopular() {
        String urlString="https://api.themoviedb.org/3/movie/popular?language=es-ES";

        return initializer(urlString);
    }

    /*
    @GetMapping("movie/{id}")
    public ResponseEntity<String> getMovie(@PathVariable Integer id) {
        String urlString="https://api.themoviedb.org/3/movie/"+id+"?language=es-ES";

        return initializer(urlString);
    }
    */

    @GetMapping("movie/{id}/credits")
    public ResponseEntity<String> getMovieCredits(@PathVariable Integer id) {
        String urlString="https://api.themoviedb.org/3/movie/"+id+"/credits?language=es-ES";

        return initializer(urlString);
    }

    @GetMapping("movie/{id}/images")
    public ResponseEntity<String> getMovieImages(@PathVariable Integer id) {
        String urlString="https://api.themoviedb.org/3/movie/"+id+"/images";

        return initializer(urlString);
    }

    @GetMapping("movie/{id}/keyword")
    public ResponseEntity<String> getMovieKeyWords(@PathVariable Integer id) {
        String urlString="https://api.themoviedb.org/3/movie/"+id+"/keywords";

        return initializer(urlString);
    }

    @GetMapping("movie/{id}/recommendations")
    public ResponseEntity<String> getMovieRecommendations(@PathVariable Integer id) {
        String urlString="https://api.themoviedb.org/3/movie/"+id+"/recommendations";

        return initializer(urlString);
    }

    @GetMapping("movie/{id}/similar")
    public ResponseEntity<String> getMovieSimilar(@PathVariable Integer id) {
        String urlString="https://api.themoviedb.org/3/movie/"+id+"/similar";

        return initializer(urlString);
    }

    /*
    @PatchMapping("/movie/{movie_id}")
    public ResponseEntity<String> postMovie(@PathVariable int movie_id, @RequestBody User_movie user_movie) throws SQLException {
        String urlString="https://api.themoviedb.org/3/movie/"+movie_id+"?language=es-ES";    
        //Recuperacion del nombre de usuario
        String currentUserName = "";
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            //hay autenticacion
            currentUserName = authentication.getName();
            System.out.println(currentUserName);
            int userId = SQLQueries.retrieveUserId(currentUserName);
            user_movie.setUserid(userId);
            try {
                SQLQueries.insertRecord(user_movie,movie_id);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return initializer(urlString);
    }
*/

    @PatchMapping("/movie/{movie_id}")
    public ResponseEntity<JsonObject> postMovie(@PathVariable int movie_id, @RequestBody User_movie user_movie)
            throws SQLException {
        String resourceUrl = "https://api.themoviedb.org/3/movie/" + movie_id + "?language=es-ES";



    String currentUserName = "";
        int userId = 0;



    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {



        currentUserName = authentication.getName();
            System.out.println(currentUserName);
            userId = SQLQueries.retrieveUserId(currentUserName);
            user_movie.setUserid(userId);
            try {



            User_movie user = SQLQueries.getUser_movieByID(movie_id, userId);



            if (user == null) {
                SQLQueries.insertRecord(user_movie, movie_id);
                } else {
                    SQLQueries.updateRecord(user_movie, movie_id);
                }



        } catch (SQLException e) {



            e.printStackTrace();
            }
        }

        User_movie userMovie = SQLQueries.getUser_movieByID(movie_id, userId);
        ResponseEntity<String> responseEntity = initializer(resourceUrl);
        //SQLQueries.exchange(resourceUrl, HttpMethod.GET, request,
        //        String.class);
        String jsonResponse = responseEntity.getBody();



    if (userMovie == null) {
            String json = jsonResponse.substring(0, jsonResponse.length() - 1)
                    .concat(",\"favourite\": \"false\", \"personal_rating\":\"null\", \"notes\": \"null\"}");



        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();



        return responseEntity.ok(jsonObject);
        } else {
            String json = jsonResponse.substring(0, jsonResponse.length() - 1)
                    .concat(",\"favourite\": \"" + userMovie.isFavourite() + "\", \"personal_rating\":\""
                            + userMovie.getPersonal_rating() + "\", \"notes\": \"" + userMovie.getNotes() + "\"}");



        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            return responseEntity.ok(jsonObject);
        }



    }

    @GetMapping("/movie/{movie_id}")
    public ResponseEntity<JsonObject> getMovie(@PathVariable String movie_id) throws SQLException {
        String urlString="https://api.themoviedb.org/3/movie/"+movie_id+"?language=es-ES";
        
        ResponseEntity<String> responseEntity = initializer(urlString);
        String jsonResponse = responseEntity.getBody();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUserName = authentication.getName();
        int userId = SQLQueries.retrieveUserId(currentUserName);
        User_movie userMovie = SQLQueries.getUser_movieByID(Integer.parseInt(movie_id), userId);
        
        if(userMovie == null) {
             String json = jsonResponse.substring(0, jsonResponse.length()-1).concat(",\"favourite\": \"false\", \"personal_rating\":\"null\", \"notes\": \"null\"}");
        
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            
            return responseEntity.ok(jsonObject);
        }
        else {
            String json = jsonResponse.substring(0, jsonResponse.length()-1).concat(",\"favourite\": \"" + userMovie.isFavourite() + "\", \"personal_rating\":\"" + userMovie.getPersonal_rating() + "\", \"notes\": \"" + userMovie.getNotes() + "\"}");
            
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            return responseEntity.ok(jsonObject);
        }
        
         }
        return responseEntity.ok(new JsonParser().parse("{}").getAsJsonObject());
    }

}
