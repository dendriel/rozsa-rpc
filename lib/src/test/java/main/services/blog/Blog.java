package main.services.blog;

import com.rozsa.rpc.annotations.RpcProcedure;
import com.rozsa.rpc.annotations.RpcService;

import java.util.*;

@RpcService("blog")
public class Blog {

    public static class Post {
        private String text;
        private String author;
        private String stars;
        private Date createdAt;

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

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
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
    public void createAll(List<Post> post, Date createdAt) {
        post.forEach(p -> p.setCreatedAt(createdAt));
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
    public List<Post> read(List<Integer> count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(List<Integer> count, int a) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(int count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(String count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(String input, int count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read() {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(Map<String, Integer> count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(Collection<String> count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(Queue<String> count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(Map<Integer, String> count, int a, int b) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(double a, Deque<Integer> count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(double a, int b, Set<Integer> count) {
        return posts;
    }

    @RpcProcedure
    public List<Post> read(double a, Integer[] count, int b) {
        return posts;
    }

}
