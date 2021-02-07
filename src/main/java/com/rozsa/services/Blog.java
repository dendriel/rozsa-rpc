package com.rozsa.services;

import com.rozsa.rpc.RpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RpcService
public class Blog {

    public static class Post {
        private String text;
        private String author;
        private String stars;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getStars() {
            return stars;
        }

        public void setStars(String stars) {
            this.stars = stars;
        }
    }


    private List<Post> posts;


    public Blog() {
        posts = new ArrayList<>();
    }

    public void create(Post post) {
        posts.add(post);
    }

    // TODO: allow user to specify object type to use lists.
//    public void createAll(List<Post> post) {
//        posts.addAll(post);
//    }

    public void createAll(Post[] post) {
        posts.addAll(Arrays.asList(post));
    }

    public List<Post> read(int count) {
        return posts;
    }
}
