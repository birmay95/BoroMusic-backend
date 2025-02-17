package com.example.music_platform.model;

//public enum Genre {
//    UNKNOWN(0),
//    ROCK(1L << 0),           // 0001
//    POP(1L << 1),            // 0010
//    JAZZ(1L << 2),           // 0100
//    HIP_HOP(1L << 3),        // 1000
//    INDIE_POP(1L << 4),
//    BLUES(1L << 5),
//    CLASSICAL(1L << 6),
//    COUNTRY(1L << 7),
//    DANCE(1L << 8),
//    DISCO(1L << 9),
//    ELECTRONIC(1L << 10),
//    FOLK(1L << 11),
//    FUNK(1L << 12),
//    METAL(1L << 13),
//    OPERA(1L << 14),
//    PUNK(1L << 15),
//    REGGAE(1L << 16),
//    RNB(1L << 17),           // R&B
//    SOUL(1L << 18),
//    TECHNO(1L << 19),
//    TRANCE(1L << 20),
//    HOUSE(1L << 21),
//    DRUM_AND_BASS(1L << 22),
//    DUBSTEP(1L << 23),
//    EDM(1L << 24),           // Electronic Dance Music
//    AMBIENT(1L << 25),
//    LATIN(1L << 26),
//    SALSA(1L << 27),
//    TANGO(1L << 28),
//    HIP_HOP_RAP(1L << 29),
//    ALTERNATIVE(1L << 30),
//    GRUNGE(1L << 31),
//    NEW_WAVE(1L << 32),
//    SYNTH_POP(1L << 33),
//    INDIE_ROCK(1L << 34),
//    LOFI(1L << 35),
//    K_POP(1L << 36),
//    J_POP(1L << 37),
//    REGGAETON(1L << 38),
//    SKA(1L << 39),
//    GARAGE(1L << 40),
//    GLAM_ROCK(1L << 41),
//    HARD_ROCK(1L << 42),
//    HEAVY_METAL(1L << 43),
//    SPEED_METAL(1L << 44),
//    BLACK_METAL(1L << 45),
//    DEATH_METAL(1L << 46),
//    THRASH_METAL(1L << 47),
//    PROGRESSIVE_ROCK(1L << 48),
//    PROGRESSIVE_METAL(1L << 49),
//    PSYCHEDELIC(1L << 50),
//    GARAGE_ROCK(1L << 51),
//    INDIE_FOLK(1L << 52),
//    BLUEGRASS(1L << 53),
//    GOSPEL(1L << 54),
//    CHILLWAVE(1L << 55),
//    SYNTHWAVE(1L << 56),
//    FUTURE_BASS(1L << 57),
//    VOCAL_JAZZ(1L << 58),
//    BEBOP(1L << 59),
//    SWING(1L << 60),
//    BOSSA_NOVA(1L << 61),
//    SALSA_BACHATA(1L << 62),
//    WORLD_MUSIC(1L << 63);   // последняя доступная позиция в long
//
//    private final long bitmask;
//
//    Genre(long bitmask) {
//        this.bitmask = bitmask;
//    }
//
//    public long getBitmask() {
//        return bitmask;
//    }
//
//    public static Genre fromString(String genreName) {
//        if (genreName == null) {
//            return UNKNOWN;
//        }
//
//        switch (genreName.trim().toLowerCase()) {
//            case "rock":
//                return ROCK;
//            case "pop":
//                return POP;
//            case "jazz":
//                return JAZZ;
//            case "hip hop":
//            case "hip-hop":
//                return HIP_HOP;
//            case "indie pop":
//            case "indie-pop":
//                return INDIE_POP;
//            case "blues":
//                return BLUES;
//            case "classical":
//                return CLASSICAL;
//            case "country":
//                return COUNTRY;
//            case "dance":
//                return DANCE;
//            case "disco":
//                return DISCO;
//            case "electronic":
//                return ELECTRONIC;
//            case "folk":
//                return FOLK;
//            case "funk":
//                return FUNK;
//            case "metal":
//                return METAL;
//            case "opera":
//                return OPERA;
//            case "punk":
//                return PUNK;
//            case "reggae":
//                return REGGAE;
//            case "rnb":
//            case "r&b":
//                return RNB;
//            case "soul":
//                return SOUL;
//            case "techno":
//                return TECHNO;
//            case "trance":
//                return TRANCE;
//            case "house":
//                return HOUSE;
//            case "drum and bass":
//            case "dnb":
//                return DRUM_AND_BASS;
//            case "dubstep":
//                return DUBSTEP;
//            case "edm":
//                return EDM;
//            case "ambient":
//                return AMBIENT;
//            case "latin":
//                return LATIN;
//            case "salsa":
//                return SALSA;
//            case "tango":
//                return TANGO;
//            case "hip hop rap":
//            case "rap":
//                return HIP_HOP_RAP;
//            case "alternative":
//                return ALTERNATIVE;
//            case "grunge":
//                return GRUNGE;
//            case "new wave":
//                return NEW_WAVE;
//            case "synth pop":
//                return SYNTH_POP;
//            case "indie rock":
//                return INDIE_ROCK;
//            case "lofi":
//                return LOFI;
//            case "k-pop":
//                return K_POP;
//            case "j-pop":
//                return J_POP;
//            case "reggaeton":
//                return REGGAETON;
//            case "ska":
//                return SKA;
//            case "garage":
//                return GARAGE;
//            case "glam rock":
//                return GLAM_ROCK;
//            case "hard rock":
//                return HARD_ROCK;
//            case "heavy metal":
//                return HEAVY_METAL;
//            case "speed metal":
//                return SPEED_METAL;
//            case "black metal":
//                return BLACK_METAL;
//            case "death metal":
//                return DEATH_METAL;
//            case "thrash metal":
//                return THRASH_METAL;
//            case "progressive rock":
//                return PROGRESSIVE_ROCK;
//            case "progressive metal":
//                return PROGRESSIVE_METAL;
//            case "psychedelic":
//                return PSYCHEDELIC;
//            case "garage rock":
//                return GARAGE_ROCK;
//            case "indie folk":
//                return INDIE_FOLK;
//            case "bluegrass":
//                return BLUEGRASS;
//            case "gospel":
//                return GOSPEL;
//            case "chillwave":
//                return CHILLWAVE;
//            case "synthwave":
//                return SYNTHWAVE;
//            case "future bass":
//                return FUTURE_BASS;
//            case "vocal jazz":
//                return VOCAL_JAZZ;
//            case "bebop":
//                return BEBOP;
//            case "swing":
//                return SWING;
//            case "bossa nova":
//                return BOSSA_NOVA;
//            case "salsa bachata":
//                return SALSA_BACHATA;
//            case "world music":
//                return WORLD_MUSIC;
//            default:
//                return UNKNOWN;
//        }
//    }
//}

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Track> tracks;
}