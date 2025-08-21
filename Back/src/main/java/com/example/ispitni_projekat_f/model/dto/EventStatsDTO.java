package com.example.ispitni_projekat_f.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventStatsDTO {
    private long eventId;
    private long likes;
    private long dislikes;
    private long views;
}
