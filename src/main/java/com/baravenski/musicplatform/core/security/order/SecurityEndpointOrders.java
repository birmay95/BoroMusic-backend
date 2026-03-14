package com.baravenski.musicplatform.core.security.order;

import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import static lombok.AccessLevel.PRIVATE;

@NullMarked
@NoArgsConstructor(access = PRIVATE)
public final class SecurityEndpointOrders {
    public static final int TRACK_UPLOAD_ORDER = 1;
}
