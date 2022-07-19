package com.printurth.event;

import lombok.Builder;
import lombok.Data;

/**
 * Created by steve on 2018. 4. 20..
 */
@Data
@Builder
public class LikeEvent {
    String likedId;
    String unlikedId;
}
