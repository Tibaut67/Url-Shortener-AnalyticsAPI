package com.tibaut.urlshortener.repository;

import com.tibaut.urlshortener.model.ClickAnalytics;
import com.tibaut.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {
    long countByUrl(Url url);
    List<ClickAnalytics> findTop10ByUrlIdOrderByClickedAtDesc(Long urlId);
}