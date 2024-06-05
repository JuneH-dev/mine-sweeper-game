package Server;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerGUI extends JFrame {
    private Timer timer;
    private AtomicBoolean gameStarted = new AtomicBoolean(false);
    private AtomicBoolean gameSetted = new AtomicBoolean(false);
    private AtomicBoolean resultSent = new AtomicBoolean(false);
    private JTextArea logArea;

    public static int inPort = 9999;
    public static Vector<Client> clients = new Vector<Client>();
    public Vector<User> allUsers = new Vector<>();
    public Vector<Client> clientsInLobby = new Vector<>();
    int width = 0, mineCount = 0;
    MyMap multiGameMap;
    JFrame frame;
    public Container mineMapCont;
    public JScrollPane multiPlayScrollPane;
    public JPanel serverInfoPanel, mapPanel, mineMapPanel, clientMapPanel, mineMapInnerPanel, clientMapInnerPanel;
    public JLabel mineCountLabel, mineMapLabel, clientMapLabel;
    public JButton[] mineMap, clientMap;

    public static void main(String[] args) throws Exception {
        new ServerGUI().createServer();
    }

    // ServerGUI 생성자 - 서버 GUI 설정
    public ServerGUI() {
        setTitle("Server");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("나눔고딕", Font.PLAIN, 14)); // 글꼴 설정
        logArea.setBackground(new Color(43, 43, 43)); // 배경색 설정
        logArea.setForeground(new Color(255, 255, 255)); // 글자색 설정

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBackground(new Color(43, 43, 43));
        scrollPane.setForeground(new Color(255, 255, 255));
        scrollPane.setBorder(new LineBorder(new Color(255, 255, 255))); // 경계선 설정

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // 서버 생성 및 클라이언트 연결 대기
    public void createServer() throws Exception {
        log("Server start running ..");
        ServerSocket server = new ServerSocket(inPort);
        while (true) {
            Socket socket = server.accept();
            bringAllUsersData();
            Client c = new Client(socket);
            clients.add(c);
        }
    }

    // 로그 메시지를 GUI에 출력
    public void log(String message) {
        logArea.append(message + "\n");
    }

    // 아이디가 사용 중인지 확인
    public boolean isIdUsed(String id) {
        for (User user : allUsers) {
            if (user.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // 닉네임이 사용 중인지 확인
    public boolean isNicknameUsed(String nickname) {
        for (User user : allUsers) {
            if (user.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    // 아이디와 비밀번호가 일치하는지 확인
    public boolean checkPassword(String id, String password) {
        for (User user : allUsers) {
            if (user.getId().equals(id)) {
                return (user.getPassword().equals(password));
            }
        }
        return false;
    }

    // 새로운 사용자 데이터를 파일에 저장
    public void writeUserData(User user) throws IOException {
        String csvFile = "users.csv";
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true));
        writer.write(user.getName() + "," + user.getId() + "," + user.getPassword() + "," + user.getNickname() + "\n");
        writer.close();
    }

    // 모든 사용자 데이터를 파일에서 가져오기
    public void bringAllUsersData() throws IOException {
        String csvFile = "users.csv";
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String line;
        String name, id, password, nickname;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            name = data[0];
            id = data[1];
            password = data[2];
            nickname = data[3];
            allUsers.add(new User(name, id, password, nickname));
        }
        reader.close();
    }

    // 사용자 이름 가져오기
    public String getUserName(Client c) {
        for (User user : allUsers) {
            if (user.getId().equals(c.id)) {
                return user.getName();
            }
        }
        return "";
    }

    // 사용자 닉네임 가져오기
    public String getUserNickname(Client c) {
        for (User user : allUsers) {
            if (user.getId().equals(c.id)) {
                return user.getNickname();
            }
        }
        return "";
    }

    // 로그인 메뉴 처리
    public void logInMenu(Client client) throws IOException, ClassNotFoundException {
        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "회원가입":
                    signUp(client);
                    break;
                case "로그인":
                    if (logIn(client)) {
                        gameMenu(client);
                        return;
                    } else {
                        break;
                    }
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 게임 메뉴 처리
    public void gameMenu(Client client) throws IOException, ClassNotFoundException {
        log(getUserNickname(client) + ": 게임 메뉴 입장");
        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "지뢰찾기":
                    gameOption(client);
                    break;
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 게임 옵션 처리
    public void gameOption(Client client) throws IOException, ClassNotFoundException {
        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "싱글플레이":
                    singlePlayOption(client);
                    break;
                case "멀티플레이":
                    multiPlayOption(client);
                    break;
                case "이전":
                    gameMenu(client);
                    break;
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 싱글플레이 옵션 처리
    public void singlePlayOption(Client client) throws IOException, ClassNotFoundException {
        log(getUserNickname(client) + ": 싱글플레이 메뉴 입장");
        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "혼자놀기":
                    log(getUserNickname(client) + ": 혼자놀기 입장");
                    setupSingleMapUI(client);
                    soloPlay(client);
                    gameOption(client);
                    break;
                case "기록도전":
                    challengeOption(client);
                    break;
                case "순위표":
                    rankingOption(client);
                    break;
                case "이전":
                    gameOption(client);
                    break;
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 기록도전 옵션 처리
    public void challengeOption(Client client) throws IOException, ClassNotFoundException {
        log(getUserNickname(client) + ": 기록도전 입장");
        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "쉬움":
                    setupSingleMapUI(client);
                    soloPlay(client);
                    writeScore(client, "easy.csv");
                    gameOption(client);
                    break;
                case "보통":
                    setupSingleMapUI(client);
                    soloPlay(client);
                    writeScore(client, "normal.csv");
                    gameOption(client);
                    break;
                case "어려움":
                    setupSingleMapUI(client);
                    soloPlay(client);
                    writeScore(client, "hard.csv");
                    gameOption(client);
                    break;
                case "이전":
                    singlePlayOption(client);
                    break;
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 순위표 옵션 처리
    public void rankingOption(Client client) throws IOException, ClassNotFoundException {
        log(getUserNickname(client) + ": 순위표 입장");
        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "쉬움 순위표":
                    sendRanking(client, "easy.csv");
                    break;
                case "보통 순위표":
                    sendRanking(client, "normal.csv");
                    break;
                case "어려움 순위표":
                    sendRanking(client, "hard.csv");
                    break;
                case "이전":
                    singlePlayOption(client);
                    break;
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 멀티플레이 옵션 처리
    public void multiPlayOption(Client client) throws IOException, ClassNotFoundException {
        log(getUserNickname(client) + ": 멀티플레이 옵션 입장");
        while (client.isActive) {
            String menu = client.in.readLine();
            if (menu == null) {
                client.isActive = false;
                continue;
            }
            switch (menu) {
                case "로비":
                    for (Client c : clientsInLobby) {
                        client.out.println(getUserNickname(c) + "님이 로비에 참가했습니다.");
                        if (c.isReady) {
                            client.out.println(getUserNickname(c) + "님이 준비를 완료했습니다.");
                        }
                    }
                    clientsInLobby.add(client);
                    for (Client c : clientsInLobby) {
                        c.out.println(getUserNickname(client) + "님이 로비에 참가했습니다.");
                    }
                    lobby(client);
                    break;
                case "이전":
                    gameOption(client);
                    break;
                case "로그아웃":
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    logOut(client);
                    exit(client);
                    return;
            }
        }
    }

    // 로비 타이머 시작
    private void startLobbyTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkAllReady();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
        }, 0, 1000); // 1초
    }

    // 로비 타이머 중지
    private void stopLobbyTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    // 모든 클라이언트가 준비되었는지 확인
    private void checkAllReady() throws IOException, ClassNotFoundException {
        if (isAllReady() && !gameStarted.get()) {
            gameStarted.set(true);
            for (Client c : clientsInLobby) {
                c.out.println("게임시작");
            }
            stopLobbyTimer();
        }
    }

    // 로비 처리
    private void lobby(Client client) throws IOException, ClassNotFoundException {
        client.isReady = false;
        client.isTurn = true;
        gameStarted.set(false);
        gameSetted.set(false);
        client.gameEnd = false;
        client.successCount = 0;

        log(getUserNickname(client) + ": 로비 입장");

        startLobbyTimer();

        while (client.isActive) {
            String menu = client.in.readLine();
            switch (menu) {
                case "준비":
                    ready(client);
                    break;
                case "전송":
                    sendMessageClientsInLobby(client);
                    break;
                case "나가기":
                    client.isReady = false;
                    clientsInLobby.remove(client);
                    for (Client c : clientsInLobby) {
                        c.out.println(getUserNickname(client) + "님이 로비를 떠났습니다.");
                    }
                    multiPlayOption(client);
                    break;
                case "로그아웃":
                    client.isReady = false;
                    clientsInLobby.remove(client);
                    for (Client c : clientsInLobby) {
                        c.out.println(getUserNickname(client) + "님이 로비를 떠났습니다.");
                    }
                    logOut(client);
                    logInMenu(client);
                    return;
                case "종료":
                    client.isReady = false;
                    clientsInLobby.remove(client);
                    for (Client c : clientsInLobby) {
                        c.out.println(getUserNickname(client) + "님이 로비를 떠났습니다.");
                    }
                    logOut(client);
                    exit(client);
                    return;
                case "게임시작":
                    log(getUserNickname(client) + ": 멀티 게임시작");

                    if (!gameSetted.get()) {
                        gameSetted.set(true);
                        Random random = new Random();
                        width = 7 + random.nextInt(4); // 7 ~ 10 맵 넓이 랜덤 설정
                        mineCount = 5 + random.nextInt(6); // 5 ~ 10 폭탄 수 랜덤 설정
                        setupMultiMapUI();
                    }
                    multiPlay(client);
                    break;
                case "상호작용":
                    String nickname;
                    menu = client.in.readLine();
                    switch (menu) {
                        case "귓속말":
                            nickname = client.in.readLine();
                            String msg = client.in.readLine();
                            for (Client c : clientsInLobby) {
                                if (getUserNickname(c).equals(nickname)) {
                                    c.out.println(getUserNickname(client) + "님의 귓속말: " + msg);
                                }
                            }
                            break;
                        case "회원정보":
                            nickname = client.in.readLine();
                            for (Client c : clientsInLobby) {
                                if (getUserNickname(c).equals(nickname)) {
                                    client.out.println("회원정보");
                                    client.out.println(getUserName(c));
                                    client.out.println(c.id);
                                    client.out.println(getUserNickname(c));
                                }
                            }
                            break;
                    }
                    break;
            }
        }
    }

    // 로비의 모든 클라이언트에게 메시지 전송
    private void sendMessageClientsInLobby(Client client) throws IOException {
        String msg = client.in.readLine();
        for (Client c : clientsInLobby) {
            c.out.println(getUserNickname(client) + ": " + msg);
        }
    }

    // 점수 기록을 파일에 저장
    private void writeScore(Client client, String csvFile) throws IOException {
        String nickname = getUserNickname(client);

        if (client.tryCount < 1) {
            return;
        }

        List<String[]> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line.split(","));
            }
        } catch (FileNotFoundException e) {
        }

        scores.add(new String[]{nickname, String.valueOf(client.tryCount)});

        scores.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                int score1 = Integer.parseInt(o1[1]);
                int score2 = Integer.parseInt(o2[1]);
                return Integer.compare(score1, score2);
            }
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            for (String[] score : scores) {
                writer.write(score[0] + "," + score[1] + "\n");
            }
        }
    }

    // 순위표 전송
    private void sendRanking(Client client, String csvFile) throws IOException {
        List<String[]> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line.split(","));
            }
        } catch (FileNotFoundException e) {
        }

        for (String[] score : scores) {
            client.out.println(score[0] + "," + score[1]);
        }
        client.out.println("END");
    }

    // 회원가입 처리
    public void signUp(Client client) throws IOException, ClassNotFoundException {
        log(client.socket.getInetAddress() + ": 회원가입 시도");
        String msg;

        while (!(msg = client.in.readLine()).equals("아이디 설정 완료")) {
            if (msg.equals("취소")) {
                log(client.socket.getInetAddress() + ": 회원가입 취소");
                return;
            }

            if (isIdUsed(msg)) {
                log(client.socket.getInetAddress() + ": 아이디가 이미 존재합니다.");
                client.out.println("중복");
            } else {
                client.out.println("가능");
                log(client.socket.getInetAddress() + ": 아이디 설정 완료");
            }
        }

        while (!(msg = client.in.readLine()).equals("정보 입력 완료")) {
            if (msg.equals("취소")) {
                log(client.socket.getInetAddress() + ": 회원가입 취소");
                return;
            }

            if (isNicknameUsed(msg)) {
                log(client.socket.getInetAddress() + ": 닉네임이 이미 존재합니다.");
                client.out.println("중복");
            } else {
                client.out.println("가능");
                log(client.socket.getInetAddress() + ": 닉네임 설정 완료");
            }
        }

        User user = (User) client.ois.readObject();
        writeUserData(user);
        allUsers.add(user);
        log(client.socket.getInetAddress() + ": 회원가입 완료");
    }

    // 로그인 처리
    public boolean logIn(Client client) throws IOException {
        log(client.socket.getInetAddress() + ": 로그인 시도");
        String id, password;

        id = client.in.readLine();
        if (isIdUsed(id)) {
            log(client.socket.getInetAddress() + ": 아이디 존재 o");
            client.out.println("아이디 존재");
        } else {
            log(client.socket.getInetAddress() + ": 아이디 존재 x");
            client.out.println("아이디가 존재하지 않음");
            return false;
        }

        password = client.in.readLine();
        if (checkPassword(id, password)) {
            log(client.socket.getInetAddress() + ": 비밀번호 일치 o");
            client.out.println("비밀번호 일치");
        } else {
            log(client.socket.getInetAddress() + ": 비밀번호 일치 x");
            client.out.println("비밀번호가 일치하지 않음");
            return false;
        }

        client.id = id;
        log(getUserNickname(client) + "님이 로그인했습니다");
        return true;
    }

    // 클라이언트 종료 처리
    public void exit(Client client) {
        clients.remove(client);
        client.isActive = false;
        try {
            client.in.close();
            client.out.close();
            client.ois.close();
            client.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        log(client.socket.getInetAddress() + ": 접속 종료");
    }

    // 로그아웃 처리
    public void logOut(Client client) throws IOException, ClassNotFoundException {
        if (getUserNickname(client).isEmpty()) {
            //
        } else {
            log(getUserNickname(client) + "님이 로그아웃했습니다.");
        }
    }

    // 준비 상태 토글
    public void ready(Client client) {
        if (!client.isReady) {
            client.isReady = true;
            for (Client c : clientsInLobby) {
                c.out.println(getUserNickname(client) + "님이 준비를 완료했습니다.");
            }
        } else {
            client.isReady = false;
            for (Client c : clientsInLobby) {
                c.out.println(getUserNickname(client) + "님이 준비를 해제했습니다.");
            }
        }
    }

    // 모든 클라이언트가 준비 상태인지 확인
    public boolean isAllReady() {
        for (Client client : clientsInLobby) {
            if (!client.isReady) {
                return false;
            }
        }
        return true;
    }

    // 모든 클라이언트가 턴을 종료했는지 확인
    public boolean isAllTurnFinish() {
        for (Client c : clientsInLobby) {
            if (c.isTurn) {
                return false;
            }
        }
        return true;
    }

    // 멀티게임 타이머 시작
    private void startMultiGameTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkAllTurn();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
        }, 0, 1000); // 1초
    }

    // 멀티게임 타이머 중지
    private void stopMultiGameTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    // 모든 클라이언트가 턴을 종료했는지 확인
    private void checkAllTurn() throws IOException, ClassNotFoundException {
        if (isAllTurnFinish()) {
            for (Client c : clientsInLobby) {
                c.out.println("턴종료");
                c.isTurn = true;
            }
        }
    }

    // 멀티플레이 처리
    public void multiPlay(Client client) throws IOException, ClassNotFoundException {
        resultSent.set(false);
        startMultiGameTimer();

        log(getUserNickname(client) + ": 멀티플레이 입장");
        client.out.println(width + "," + mineCount);
        String msg;
        while (client.isActive && !client.gameEnd) {
            msg = client.in.readLine();
            if (msg == null) continue;
            if (msg.equalsIgnoreCase("로그아웃")) {
                client.isReady = false;
                clientsInLobby.remove(client);
                logOut(client);
                logInMenu(client);
                return;
            } else if (msg.equalsIgnoreCase("종료")) {
                client.isReady = false;
                clientsInLobby.remove(client);
                logOut(client);
                exit(client);
                return;
            } else if (msg.equals("끝")) {
                client.gameEnd = true;
                break;
            }
            if (client.isTurn) {
                client.isTurn = false;
                String[] arr = msg.split(",");
                int x = Integer.parseInt(arr[0]);
                int y = Integer.parseInt(arr[1]);
                int buttonIndex = x * width + y;
                int result = multiGameMap.checkMine(x, y);
                client.out.println(result + "," + (x * width + y));

                if (result == 9) {
                    mineCount--;
                    client.successCount++;
                    mineCountLabel.setText("남은 폭탄: " + mineCount + "개");
                    client.clientMap[buttonIndex].setText("\uD83D\uDCA3");
                    String message = getUserNickname(client) + "님이 폭탄을 발견했습니다," + buttonIndex;
                    for (Client c : clientsInLobby) {
                        if (c != client) {
                            c.out.println(message);
                        }
                    }
                } else {
                    client.clientMap[buttonIndex].setText("X");
                    client.clientMap[buttonIndex].setForeground(Color.RED);
                }
            } else {
                //
            }
            if (multiGameMap.isAllFound()) {
                for (Client c : clientsInLobby) {
                    c.out.println("게임종료");
                }
                break;
            }
        }
        if (!resultSent.get()) {
            resultSent.set(true);
            sendResult();
        }
        stopMultiGameTimer();

        clientsInLobby.clear();
        frame.dispose();
        multiPlayOption(client);
    }

    // 결과 전송
    public void sendResult() {
        List<Client> sortedClients = new ArrayList<>(clientsInLobby);
        sortedClients.sort(Comparator.comparingInt(Client::getSuccessCount).reversed());
        int rank = 1;
        StringBuilder rankingMessage = new StringBuilder("순위:\n");
        for (Client client : sortedClients) {
            rankingMessage.append(rank).append("등: ")
                    .append(getUserNickname(client)).append(" 발견한 폭탄 수: ")
                    .append(client.successCount).append("개\n");
            rank++;
        }
        rankingMessage.append("END\n");

        for (Client client : clientsInLobby) {
            client.out.println(rankingMessage.toString());
        }
    }

    // 멀티플레이 맵 UI 설정
    public void setupMultiMapUI() {
        multiGameMap = new MyMap(width, mineCount);

        frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mineMapCont = frame.getContentPane();

        serverInfoPanel = new JPanel();
        mapPanel = new JPanel(new GridLayout((clientsInLobby.size() + 2) / 2, 2));
        mineMapPanel = new JPanel(new BorderLayout());

        mineCountLabel = new JLabel("남은 폭탄: " + mineCount + "개");
        mineCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mineCountLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        mineCountLabel.setForeground(new Color(255, 255, 255));

        mineMapLabel = new JLabel("정답");
        mineMapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mineMapLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        mineMapLabel.setForeground(new Color(255, 255, 255));

        serverInfoPanel.add(mineCountLabel);
        serverInfoPanel.setBackground(new Color(43, 43, 43));

        mineMapInnerPanel = new JPanel(new GridLayout(width, width));
        mineMapInnerPanel.setBackground(new Color(43, 43, 43));

        mineMapPanel.add(mineMapLabel, BorderLayout.NORTH);
        mineMapPanel.add(mineMapInnerPanel, BorderLayout.CENTER);
        mineMapPanel.setBorder(new LineBorder(new Color(0, 255, 0), 2));
        mineMapPanel.setBackground(new Color(43, 43, 43));

        mapPanel.add(mineMapPanel);
        mapPanel.setBackground(new Color(43, 43, 43));

        for (Client c : clientsInLobby) {
            synchronized (c) {
                c.clientMapPanel = new JPanel(new BorderLayout());
                c.clientInnerPanel = new JPanel(new GridLayout(width, width));
                c.clientInnerPanel.setBackground(new Color(43, 43, 43));
                c.clientMapLabel = new JLabel(getUserNickname(c));
                c.clientMapLabel.setHorizontalAlignment(SwingConstants.CENTER);
                c.clientMapLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
                c.clientMapLabel.setForeground(new Color(255, 255, 255));
                c.clientMapPanel.add(c.clientMapLabel, BorderLayout.NORTH);
                c.clientMapPanel.add(c.clientInnerPanel, BorderLayout.CENTER);
                c.clientMapPanel.setBorder(new LineBorder(new Color(0, 255, 0), 2));
                c.clientMapPanel.setBackground(new Color(43, 43, 43));
                mapPanel.add(c.clientMapPanel);

                c.clientMap = new JButton[width * width];
                for (int i = 0; i < width * width; i++) {
                    c.clientMap[i] = new JButton();
                    c.clientMap[i].setBackground(Color.DARK_GRAY);
                    c.clientMap[i].setForeground(Color.RED);
                    c.clientMap[i].setBorder(new LineBorder(Color.WHITE));
                    c.clientMap[i].setPreferredSize(new Dimension(50, 50));
                    c.clientMap[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
                    c.clientInnerPanel.add(c.clientMap[i]);
                }
            }
        }

        multiPlayScrollPane = new JScrollPane(mapPanel);
        multiPlayScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        multiPlayScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mineMapCont.setLayout(new BorderLayout());
        mineMapCont.add(serverInfoPanel, BorderLayout.NORTH);
        mineMapCont.add(multiPlayScrollPane, BorderLayout.CENTER);
        mineMapCont.setBackground(new Color(43, 43, 43));

        mineMap = new JButton[width * width];

        for (int i = 0; i < width * width; i++) {
            int x = i / width;
            int y = i % width;

            mineMap[i] = new JButton();
            mineMap[i].setBackground(Color.DARK_GRAY);
            mineMap[i].setForeground(Color.RED);
            mineMap[i].setBorder(new LineBorder(Color.WHITE));
            mineMap[i].setPreferredSize(new Dimension(50, 50));
            mineMap[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
            if (multiGameMap.setMineMap(x, y) == 1) {
                mineMap[i].setText("\uD83D\uDCA3");
            }
            mineMapInnerPanel.add(mineMap[i]);
        }

        frame.pack();
        frame.setVisible(true);
    }

    // 싱글플레이 처리
    public void soloPlay(Client client) {
        client.tryCount = 0;
        String msg;
        int mineNum = mineCount;

        try {
            while (client.isActive) {
                if (client.singleGameMap.isAllFound()) {
                    client.tryCount = Integer.parseInt(client.in.readLine());
                    if (client.tryCount < mineNum) {
                        client.tryCount = 0;
                    }
                    frame.dispose();
                    return;
                }
                msg = client.in.readLine();
                if (msg == null) continue;
                if (msg.equalsIgnoreCase("로그아웃")) {
                    frame.dispose();
                    logOut(client);
                    logInMenu(client);
                    return;
                } else if (msg.equalsIgnoreCase("종료")) {
                    frame.dispose();
                    logOut(client);
                    exit(client);
                    return;
                }

                String[] arr = msg.split(",");
                int x = Integer.parseInt(arr[0]);
                int y = Integer.parseInt(arr[1]);

                int result = client.singleGameMap.checkMine(x, y);
                client.out.println("" + result);

                int buttonIndex = x * width + y;
                if (result == 9) {
                    clientMap[buttonIndex].setText("\uD83D\uDCA3");
                    mineCount--;
                    mineCountLabel.setText("남은 폭탄: " + mineCount + "개");
                } else {
                    clientMap[buttonIndex].setText("X");
                    clientMap[buttonIndex].setForeground(Color.RED);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
    }

    // 싱글플레이 맵 UI 설정
    public void setupSingleMapUI(Client client) throws IOException, ClassNotFoundException {
        String msg = client.in.readLine();
        if (msg.equalsIgnoreCase("로그아웃")) {
            logOut(client);
            logInMenu(client);
            return;
        } else if (msg.equalsIgnoreCase("종료")) {
            logOut(client);
            exit(client);
            return;
        }
        String[] arr = msg.split(",");
        width = Integer.parseInt(arr[0]);
        mineCount = Integer.parseInt(arr[1]);

        client.singleGameMap = new MyMap(width, mineCount);

        frame = new JFrame();
        frame.setTitle(getUserNickname(client) + "맵");
        frame.setResizable(false);
        mineMapCont = frame.getContentPane();
        mineMapCont.setBackground(new Color(43, 43, 43));

        serverInfoPanel = new JPanel();
        mapPanel = new JPanel(new GridLayout(1, 2));
        mineMapPanel = new JPanel(new BorderLayout());
        clientMapPanel = new JPanel(new BorderLayout());

        mineCountLabel = new JLabel("남은 폭탄: " + mineCount + "개");
        mineCountLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        mineCountLabel.setForeground(new Color(255, 255, 255));

        mineMapLabel = new JLabel("폭탄 맵");
        mineMapLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        mineMapLabel.setForeground(new Color(255, 255, 255));

        clientMapLabel = new JLabel("클라이언트 맵");
        clientMapLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        clientMapLabel.setForeground(new Color(255, 255, 255));

        serverInfoPanel.add(mineCountLabel);
        serverInfoPanel.setBackground(new Color(43, 43, 43));

        mineMapInnerPanel = new JPanel(new GridLayout(width, width));
        mineMapInnerPanel.setBackground(new Color(43, 43, 43));

        clientMapInnerPanel = new JPanel(new GridLayout(width, width));
        clientMapInnerPanel.setBackground(new Color(43, 43, 43));

        mineMapPanel.add(mineMapLabel, BorderLayout.NORTH);
        mineMapPanel.add(mineMapInnerPanel, BorderLayout.CENTER);
        mineMapPanel.setBorder(new LineBorder(new Color(0, 255, 0), 2));

        mineMapPanel.setBackground(new Color(43, 43, 43));

        clientMapPanel.add(clientMapLabel, BorderLayout.NORTH);
        clientMapPanel.add(clientMapInnerPanel, BorderLayout.CENTER);
        clientMapPanel.setBorder(new LineBorder(new Color(0, 255, 0), 2));
        clientMapPanel.setBackground(new Color(43, 43, 43));

        mapPanel.add(mineMapPanel);
        mapPanel.add(clientMapPanel);
        mapPanel.setBackground(new Color(43, 43, 43));

        mineMapCont.add(serverInfoPanel, BorderLayout.NORTH);
        mineMapCont.add(mapPanel, BorderLayout.CENTER);

        mineMap = new JButton[width * width];
        clientMap = new JButton[width * width];

        for (int i = 0; i < width * width; i++) {
            int x = i / width;
            int y = i % width;

            mineMap[i] = new JButton();
            mineMap[i].setBackground(Color.DARK_GRAY);
            mineMap[i].setForeground(Color.RED);
            mineMap[i].setBorder(new LineBorder(Color.WHITE));
            mineMap[i].setPreferredSize(new Dimension(50, 50));
            mineMap[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
            if (client.singleGameMap.setMineMap(x, y) == 1) {
                mineMap[i].setText("\uD83D\uDCA3");
            }
            mineMapInnerPanel.add(mineMap[i]);

            clientMap[i] = new JButton();
            clientMap[i].setBackground(Color.DARK_GRAY);
            clientMap[i].setForeground(Color.RED);
            clientMap[i].setBorder(new LineBorder(Color.WHITE));
            clientMap[i].setPreferredSize(new Dimension(50, 50));
            clientMap[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
            clientMapInnerPanel.add(clientMap[i]);
        }
        frame.pack();

        frame.setVisible(true);
    }

    // 클라이언트 클래스
    class Client extends Thread {
        Socket socket;
        PrintWriter out = null;
        BufferedReader in = null;
        ObjectInputStream ois;

        volatile boolean isActive = true;

        String id;
        volatile boolean isTurn = true;
        int successCount;
        int tryCount = 0;
        boolean isReady = false;
        boolean gameEnd = false;
        MyMap singleGameMap;

        public JPanel clientMapPanel, clientInnerPanel;
        public JLabel clientMapLabel;
        public JButton[] clientMap;

        public Client(Socket socket) throws Exception {
            log(socket.getInetAddress() + ": 접속");
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ois = new ObjectInputStream(socket.getInputStream());

            start();
        }

        public int getSuccessCount() {
            return successCount;
        }

        @Override
        public void run() {
            try {
                logInMenu(this);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
