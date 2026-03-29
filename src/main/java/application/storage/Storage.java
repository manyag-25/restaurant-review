package application.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import application.exception.InvalidArgumentException;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;

/**
 * Class representing the storage of reviews.
 */
public class Storage {
    private static final String STORAGE_HEADER = "MEALMETER_V1";
    private static final String REVIEW_SEPARATOR = "---";

    private static final String KEY_FOOD = "food";
    private static final String KEY_CLEANLINESS = "cleanliness";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_RESOLVED = "resolved";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_BODY = "body";

    private final Path storageFilePath;

    /**
     * Constructs a Storage object with a default storage file path.
     */
    public Storage() {
        this(Paths.get("data", "reviews.txt"));
    }

    /**
     * Constructs a Storage object with an injectable file path.
     *
     * @param storageFilePath the path to the storage file
     */
    public Storage(Path storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    /**
     * Loads reviews from the storage file.
     *
     * @return review list loaded from storage
     * @throws IOException if file operations fail
     */
    public ReviewList loadReviews() throws IOException {
        return loadReviewsWithWarnings().reviewList();
    }

    /**
     * Loads reviews from storage and returns non-fatal loading warnings.
     *
     * @return storage load result containing reviews and warnings
     * @throws IOException if file operations fail
     */
    public StorageLoadResult loadReviewsWithWarnings() throws IOException {
        ensureStorageFileExists();

        List<String> lines = Files.readAllLines(storageFilePath, StandardCharsets.UTF_8);
        List<String> warnings = new ArrayList<>();

        if (lines.isEmpty()) {
            writeHeaderOnly();
            return new StorageLoadResult(new ReviewList(), Collections.emptyList());
        }

        int startIndex = 0;
        if (STORAGE_HEADER.equals(lines.get(0))) {
            startIndex = 1;
        } else {
            warnings.add("Storage header missing or invalid. Attempting best-effort load.");
        }

        List<Review> loadedReviews = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();

        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);

            if (REVIEW_SEPARATOR.equals(line)) {
                parseAndAddBlock(currentBlock, loadedReviews, warnings);
                currentBlock.clear();
                continue;
            }

            if (line.isBlank() && currentBlock.isEmpty()) {
                continue;
            }

            currentBlock.add(line);
        }

        parseAndAddBlock(currentBlock, loadedReviews, warnings);
        return new StorageLoadResult(new ReviewList(loadedReviews), Collections.unmodifiableList(warnings));
    }

    /**
     * Saves all reviews to the storage file.
     *
     * @param reviewList the review list to save
     * @throws IOException if file operations fail
     */
    public void saveReviews(ReviewList reviewList) throws IOException {
        ensureStorageFileExists();

        List<String> output = new ArrayList<>();
        output.add(STORAGE_HEADER);

        for (Review review : reviewList.getAllReviews()) {
            output.add(KEY_FOOD + "=" + review.getRating().getFoodScore());
            output.add(KEY_CLEANLINESS + "=" + review.getRating().getCleanlinessScore());
            output.add(KEY_SERVICE + "=" + review.getRating().getServiceScore());
            output.add(KEY_RESOLVED + "=" + review.isResolved());
            output.add(KEY_TAGS + "=" + escape(serializeTags(review.getTags())));
            output.add(KEY_BODY + "=" + escape(review.getReviewBody()));
            output.add(REVIEW_SEPARATOR);
        }

        Files.write(
                storageFilePath,
                output,
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
    }

    private void parseAndAddBlock(List<String> blockLines, List<Review> loadedReviews, List<String> warnings) {
        if (blockLines.isEmpty()) {
            return;
        }

        try {
            Review review = parseReviewBlock(blockLines);
            loadedReviews.add(review);
        } catch (InvalidArgumentException | IllegalArgumentException e) {
            warnings.add("Skipped malformed review block. " + e.getMessage());
        }
    }

    private Review parseReviewBlock(List<String> blockLines) throws InvalidArgumentException {
        Map<String, String> valueByKey = new HashMap<>();

        for (String line : blockLines) {
            if (line.isBlank()) {
                continue;
            }

            int delimiterIndex = line.indexOf('=');
            if (delimiterIndex <= 0) {
                throw new InvalidArgumentException("Invalid key-value line: " + line);
            }

            String key = line.substring(0, delimiterIndex).trim();
            String value = line.substring(delimiterIndex + 1);
            valueByKey.put(key, value);
        }

        if (!valueByKey.containsKey(KEY_FOOD)
                || !valueByKey.containsKey(KEY_CLEANLINESS)
                || !valueByKey.containsKey(KEY_SERVICE)
                || !valueByKey.containsKey(KEY_RESOLVED)
                || !valueByKey.containsKey(KEY_BODY)
                || !valueByKey.containsKey(KEY_TAGS)) {
            throw new InvalidArgumentException("Missing required fields in review block.");
        }

        double foodScore;
        double cleanlinessScore;
        double serviceScore;

        try {
            foodScore = Double.parseDouble(valueByKey.get(KEY_FOOD));
            cleanlinessScore = Double.parseDouble(valueByKey.get(KEY_CLEANLINESS));
            serviceScore = Double.parseDouble(valueByKey.get(KEY_SERVICE));
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Invalid numeric value in review block.");
        }

        String resolvedAsString = valueByKey.get(KEY_RESOLVED).trim();
        boolean isResolved;
        if ("true".equalsIgnoreCase(resolvedAsString)) {
            isResolved = true;
        } else if ("false".equalsIgnoreCase(resolvedAsString)) {
            isResolved = false;
        } else {
            throw new InvalidArgumentException("Invalid resolved value in review block.");
        }

        String body = unescape(valueByKey.get(KEY_BODY));
        String tagsAsString = unescape(valueByKey.get(KEY_TAGS));

        Rating rating = new Rating(foodScore, cleanlinessScore, serviceScore);
        Review review = new Review(body, rating, Tag.toTags(tagsAsString));

        if (isResolved) {
            review.markResolved();
        }

        return review;
    }

    private String serializeTags(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getTagName)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String escape(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private String unescape(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '\\' && i + 1 < input.length()) {
                char nextChar = input.charAt(i + 1);

                if (nextChar == 'n') {
                    output.append('\n');
                    i++;
                    continue;
                }

                if (nextChar == 'r') {
                    output.append('\r');
                    i++;
                    continue;
                }

                if (nextChar == '\\') {
                    output.append('\\');
                    i++;
                    continue;
                }
            }

            output.append(currentChar);
        }

        return output.toString();
    }

    private void ensureStorageFileExists() throws IOException {
        Path parentDirectory = storageFilePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (!Files.exists(storageFilePath)) {
            writeHeaderOnly();
        }
    }

    private void writeHeaderOnly() throws IOException {
        Files.write(
                storageFilePath,
                List.of(STORAGE_HEADER),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
