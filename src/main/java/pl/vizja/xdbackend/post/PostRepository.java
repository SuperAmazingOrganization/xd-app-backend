package pl.vizja.xdbackend.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Post findPostById(Long id);
}
