package com.example.prj3.utils;

import com.example.prj3.models.Movie;
import com.example.prj3.models.Showtime;
import com.example.prj3.models.Theater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Seed dữ liệu mẫu vào Firebase Realtime Database.
 * Chỉ chạy một lần khi chưa có dữ liệu.
 */
public class DataSeeder {

    public static void seedIfEmpty() {
        FirebaseDatabase.getInstance().getReference("movies")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                        seedTheaters();
                        seedMovies();
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            });
    }

    // ==================== THEATERS ====================
    private static void seedTheaters() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        Object[][] theaters = {
            {"CGV Vincom Bà Triệu",       "191 Bà Triệu, Hai Bà Trưng",     "Hà Nội",  "024 7300 6600", 8},
            {"Lotte Cinema Landmark",      "72A Nguyễn Thị Minh Khai, Q.3",   "TP.HCM",  "028 3822 1234", 10},
            {"BHD Star Phạm Ngọc Thạch",  "34 Phạm Ngọc Thạch, Q.3",        "TP.HCM",  "028 3930 2222", 6},
            {"Galaxy Nguyễn Du",           "116 Nguyễn Du, Q.1",              "TP.HCM",  "028 3925 6666", 7},
            {"CGV Aeon Mall Long Biên",    "27 Cổ Linh, Long Biên",           "Hà Nội",  "024 7300 6601", 9},
        };

        for (Object[] t : theaters) {
            String key = db.getReference("theaters").push().getKey();
            Theater theater = new Theater(key,
                (String) t[0], (String) t[1], (String) t[2],
                (String) t[3], (Integer) t[4]);
            db.getReference("theaters").child(key).setValue(theater);
        }
    }

    // ==================== MOVIES ====================
    private static void seedMovies() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        String key1 = db.getReference("movies").push().getKey();
        db.getReference("movies").child(key1).setValue(new Movie(key1,
            "Avengers: Endgame",
            "Sau sự kiện Infinity War, các siêu anh hùng còn lại tập hợp lại để đảo ngược hành động tàn phá của Thanos và khôi phục vũ trụ.",
            "Hành động / Sci-Fi", "Tiếng Anh",
            "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
            8.4, 181, 2024, true, false));

        String key2 = db.getReference("movies").push().getKey();
        db.getReference("movies").child(key2).setValue(new Movie(key2,
            "Inception",
            "Một tên trộm chuyên đánh cắp bí mật từ tâm trí người khác được giao nhiệm vụ cấy ghép ý tưởng vào tâm trí một CEO quyền lực.",
            "Sci-Fi / Hành động", "Tiếng Anh",
            "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
            8.8, 148, 2023, true, false));

        String key3 = db.getReference("movies").push().getKey();
        db.getReference("movies").child(key3).setValue(new Movie(key3,
            "The Dark Knight",
            "Batman đối mặt với The Joker, kẻ phản diện hỗn loạn muốn nhấn chìm Gotham City vào hỗn loạn và tội ác.",
            "Hành động / Tội phạm", "Tiếng Anh",
            "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            9.0, 152, 2024, true, false));

        String key4 = db.getReference("movies").push().getKey();
        db.getReference("movies").child(key4).setValue(new Movie(key4,
            "Lật Mặt 7",
            "Bộ phim hài gia đình Việt Nam về những tình huống dở khóc dở cười xoay quanh cuộc sống thường ngày của người Việt.",
            "Hài / Gia đình", "Tiếng Việt",
            "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            7.5, 110, 2026, true, false));

        String key5 = db.getReference("movies").push().getKey();
        db.getReference("movies").child(key5).setValue(new Movie(key5,
            "Interstellar 2",
            "Hành trình tiếp theo khám phá không gian của nhân loại, vượt qua các chiều không gian tìm kiếm ngôi nhà mới.",
            "Sci-Fi / Phiêu lưu", "Tiếng Anh",
            "https://image.tmdb.org/t/p/w500/rAiYTfKGqDCRIIqo664sY9XZIvQ.jpg",
            0.0, 169, 2026, false, true));

        String key6 = db.getReference("movies").push().getKey();
        db.getReference("movies").child(key6).setValue(new Movie(key6,
            "Deadpool & Wolverine 2",
            "Cặp đôi kỳ quặc nhất Marvel trở lại với những pha hành động điên rồ và hài hước không thể đoán trước.",
            "Hành động / Hài", "Tiếng Anh",
            "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            0.0, 127, 2026, false, true));

        // Seed showtimes cho phim đang chiếu
        seedShowtimes(key1);
        seedShowtimes(key2);
        seedShowtimes(key3);
        seedShowtimes(key4);
    }

    // ==================== SHOWTIMES ====================
    private static void seedShowtimes(String movieId) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // [theater, date dd/MM/yyyy, time HH:mm, price]
        Object[][] data = {
            {"CGV Vincom Bà Triệu",      "15/04/2026", "10:00", 90000L},
            {"CGV Vincom Bà Triệu",      "15/04/2026", "14:00", 90000L},
            {"CGV Vincom Bà Triệu",      "15/04/2026", "20:00", 110000L},
            {"Lotte Cinema Landmark",    "15/04/2026", "11:30", 95000L},
            {"Lotte Cinema Landmark",    "15/04/2026", "16:00", 95000L},
            {"BHD Star Phạm Ngọc Thạch", "16/04/2026", "13:00", 85000L},
            {"BHD Star Phạm Ngọc Thạch", "16/04/2026", "18:30", 85000L},
            {"Galaxy Nguyễn Du",         "16/04/2026", "09:30", 80000L},
            {"Galaxy Nguyễn Du",         "16/04/2026", "15:00", 80000L},
            {"CGV Aeon Mall Long Biên",  "17/04/2026", "12:00", 90000L},
            {"CGV Aeon Mall Long Biên",  "17/04/2026", "19:00", 100000L},
        };

        for (Object[] row : data) {
            String key = db.getReference("showtimes").push().getKey();
            Showtime s = new Showtime(key, movieId,
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (Long)   row[3],
                50, 80);
            db.getReference("showtimes").child(key).setValue(s);
        }
    }
}
