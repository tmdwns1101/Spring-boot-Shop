package com.shop.constant;

import java.time.LocalDateTime;

public enum SearchDateType {
    ALL {
        @Override
        public LocalDateTime calcDate(LocalDateTime now) {
            return null;
        }
    },
    ONE_DAY {
        @Override
        public LocalDateTime calcDate(LocalDateTime now) {
            return null;
        }
    },
    ONE_WEEK {
        @Override
        public LocalDateTime calcDate(LocalDateTime now) {
            return null;
        }
    },
    ONE_MONTH {
        @Override
        public LocalDateTime calcDate(LocalDateTime now) {
            return null;
        }
    },
    SIX_MONTH {
        @Override
        public LocalDateTime calcDate(LocalDateTime now) {
            return null;
        }
    };

    public abstract LocalDateTime calcDate(LocalDateTime now);
}
