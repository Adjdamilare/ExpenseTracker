package com.dami.expensetracker.services;

import com.dami.expensetracker.models.Tag;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findByUser(User user) {
        return tagRepository.findByUserOrderByNameAsc(user);
    }

    /**
     * Saves a new or updated tag.
     */
    public void save(Tag tag) {
        tagRepository.save(tag);
    }

    /**
     * Checks if a tag with the given name already exists for the user.
     */
    public boolean existsByNameAndUser(String name, User user) {
        return tagRepository.existsByNameAndUser(name, user);
    }

    public List<Tag> findAllByIds(List<Integer> tagIds) {
        // This method confirms your ID is likely an Integer
        return tagRepository.findAllById(tagIds);
    }

    /**
     * FIX: Changed the parameter type from Long to Integer to match the entity's ID.
     */
    public Optional<Tag> findById(Integer id) {
        return tagRepository.findById(id);
    }
}