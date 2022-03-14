package ru.job4j.articles.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.articles.model.Article;
import ru.job4j.articles.model.Word;
import ru.job4j.articles.service.generator.ArticleGenerator;
import ru.job4j.articles.store.Store;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleArticleService implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleArticleService.class.getSimpleName());

    private final ArticleGenerator articleGenerator;

    private static WeakHashMap<Article, WeakReference<Article>> weakMap = new WeakHashMap<>();

    public SimpleArticleService(ArticleGenerator articleGenerator) {
        this.articleGenerator = articleGenerator;
    }

    @Override
    public void generate(Store<Word> wordStore, int count, Store<Article> articleStore) {
        LOGGER.info("Геренация статей в количестве {}", count);
        var words = wordStore.findAll();
        IntStream.iterate(0, i -> i < count, i -> i + 1)
                .peek(i -> LOGGER.info("Сгенерирована статья № {}", i))
                .forEach((x) -> {
                    Article key = articleGenerator.generate(words);
                    WeakReference<Article> value = new WeakReference<>(key);
                    weakMap.put(key, value);
                    articleStore.save(weakMap.get(key).get());
                });
    }
}
