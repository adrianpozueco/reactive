/*
 * Copyright (c) 2018 Ryanair Ltd. All rights reserved.
 */
package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Hut {
    private String id;
    private String name;
}
