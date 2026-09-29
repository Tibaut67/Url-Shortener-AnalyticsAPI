package com.tibaut.urlshortener.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "click_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    private String referer;
    private String userAgent;

    @CreationTimestamp
    private LocalDateTime clickedAt;
}
