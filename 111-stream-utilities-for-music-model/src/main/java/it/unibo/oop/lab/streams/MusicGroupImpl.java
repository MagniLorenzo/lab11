package it.unibo.oop.lab.streams;

import static java.util.stream.Collectors.averagingDouble;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream().map(s -> s.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.keySet().stream().filter(a -> albums.get(a).equals(year));
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) songs.stream().filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().get().equals(albumName)).count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) songs.stream().filter(s -> s.getAlbumName().isEmpty()).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return OptionalDouble.of(songs.stream().filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().get().equals(albumName)).collect(averagingDouble(s -> s.getDuration())));
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(
                songs.stream().max((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration())).get().getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return albums.keySet().stream()
                .max((a1, a2) -> Double.compare(songs.stream().filter(s -> s.getAlbumName().isPresent())
                        .filter(s -> s.getAlbumName().get().equals(a1)).map(s -> s.getDuration())
                        .reduce((d1, d2) -> d1 + d2).get(),
                        songs.stream().filter(s -> s.getAlbumName().isPresent())
                                .filter(s -> s.getAlbumName().get().equals(a2)).map(s -> s.getDuration())
                                .reduce((d1, d2) -> d1 + d2).get()));
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
