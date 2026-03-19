package application.review;

import application.parser.ArgumentParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a tag attached to a review for categorisation and filtering.
 * <p>
 * Examples of tags include {@code food}, {@code service}, and
 * {@code slow service}.
 * </p>
 */
public record Tag(String tagName) {
    /**
     * Constructs a {@code Tag} with the specified tag name.
     *
     * @param tagName the name of the tag
     * @throws IllegalArgumentException if the tag name is null or invalid
     */
    public Tag(String tagName) {
        if (!isValidTagName(tagName)) {
            throw new IllegalArgumentException("Tag name must not be null or blank.");
        }
        this.tagName = tagName.trim();
    }

    /**
     * Returns whether the given tag name is valid.
     *
     * @param tagName the tag name to validate
     * @return {@code true} if the tag name is non-null and non-blank,
     * {@code false} otherwise
     */
    private boolean isValidTagName(String tagName) {
        return tagName != null && !tagName.isBlank();
    }

    /**
     * Returns the tag name.
     *
     * @return the tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Returns a string representation of this tag.
     *
     * @return the tag name
     */
    @Override
    public String toString() {
        return tagName;
    }

    /**
     * Returns whether this tag is equal to another object.
     *
     * @param obj the object to compare against
     * @return {@code true} if both tags have the same tag name, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Tag(String name))) {
            return false;
        }

        return tagName.equalsIgnoreCase(name);
    }

    /**
     * Returns a set of Tags based on a string of tags. An empty set is returned if the string is null or empty.
     *
     * @param tagsAsString the string of tags
     * @return a set of Tag objects
     */
    public static Set<Tag> toTags(String tagsAsString) {
        if (!ArgumentParser.isValidString(tagsAsString)) {
            return new HashSet<>();
        }

        //separate tags in string format
        String[] listOfTagsAsString = tagsAsString.trim().split(" ");

        return Arrays.stream(listOfTagsAsString)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the hash code of this tag.
     *
     * @return the hash code based on the tag name
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagName.toLowerCase());
    }
}
