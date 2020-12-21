package com.cht.rst.feign.inner;

import static com.cht.rst.feign.inner.Util.checkNotNull;
import static com.cht.rst.feign.inner.Util.emptyToNull;

public interface Target<T> {

    Class<T> type();

    String name();

    String url();

    class HardCodedTarget<T> implements Target<T> {

        private final Class<T> type;
        private final String name;
        private final String url;

        public HardCodedTarget(Class<T> type, String url) {
            this(type, url, url);
        }

        public HardCodedTarget(Class<T> type, String name, String url) {
            this.type = checkNotNull(type, "type");
            this.name = checkNotNull(emptyToNull(name), "name");
            this.url = checkNotNull(emptyToNull(url), "url");
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String url() {
            return url;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HardCodedTarget) {
                HardCodedTarget<?> other = (HardCodedTarget) obj;
                return type.equals(other.type)
                        && name.equals(other.name)
                        && url.equals(other.url);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + type.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + url.hashCode();
            return result;
        }

        @Override
        public String toString() {
            if (name.equals(url)) {
                return "HardCodedTarget(type=" + type.getSimpleName() + ", url=" + url + ")";
            }
            return "HardCodedTarget(type=" + type.getSimpleName() + ", name=" + name + ", url=" + url
                    + ")";
        }
    }
}
