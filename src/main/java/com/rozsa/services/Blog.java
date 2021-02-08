package com.rozsa.services;

import com.rozsa.rpc.annotations.RpcProcedure;
import com.rozsa.rpc.annotations.RpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @RpcProcedure
    public void create(Post post) {
        posts.add(post);
    }

    @RpcProcedure
    public void createAll(List<Post> post) {
        posts.addAll(post);
    }

    @RpcProcedure
    public void createAllArray(Post[] post) {
        posts.addAll(Arrays.asList(post));
    }

    @RpcProcedure
    public void createAllMap(Map<String, Post> newPosts) {
        posts.addAll(newPosts.values());
    }

    @RpcProcedure
    public List<Post> read(int count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read() {
        return posts;
    }
}
