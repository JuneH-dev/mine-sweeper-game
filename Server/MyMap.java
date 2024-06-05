package Server;
import java.util.HashMap;
import java.util.Random;

public class MyMap {
    int width;
    int num_mine;
    HashMap<Integer, Boolean> minePosition;

    public MyMap(int width, int num_mine) {
        this.width = width;
        this.num_mine = num_mine;

        // create mines
        Random r = new Random();
        minePosition = new HashMap<>();
        for (int i = 0; i < num_mine; i++) {
            int position = r.nextInt(width * width);
            while (minePosition.containsKey(position))   // check repetition
                position = r.nextInt(width * width);
            minePosition.put(position, false);
        }
    }

    public int setMineMap(int x, int y){
        int pos = (x*width) + y;

        if (minePosition.containsKey(pos)) {
            return 1;
        } else {
            return 0;
        }
    }

    public int checkMine(int x, int y) {
        int pos = (x*width) + y;

        if (minePosition.containsKey(pos)) {
            // 발견되지 않은 폭탄
            if(!minePosition.get(pos)){
                minePosition.replace(pos, true);
                return 9;
            }
            else {
                // 이미 발견된 폭탄
                return 10;
            }
        }
        else {
            // 폭탄 없음, 주변 1칸 폭탄 개수 반환
            return getSurroundingMineCount(x,y);
        }

    }

    public int getSurroundingMineCount(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < width && j >= 0 && j < width) {
                    int pos = i * width + j;
                    if (minePosition.containsKey(pos)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public boolean isAllFound() {
        // HashMap의 모든 값이 true인지 확인
        for (Boolean value : this.minePosition.values()) {
            if (!value) {
                return false;
            }
        }
        return true;
    }
}

