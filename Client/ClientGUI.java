package Client;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends JFrame {
    static int inPort = 9999;
    static String address = "192.168.0.12";
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream oos;

    int width = 0;
    int mineCount = 0;
    int successCount = 0;
    int tryCount = 0;
    int failCount = 0;
    int onlinePlayerIndex = 0;
    boolean isTurn;

    private JPanel loginPanel;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton logInButton, signUpButton;

    private JPanel gameMenuPanel;
    private JButton gameButton;

    private JPanel controlPanel;
    private JButton logOutButton, exitButton;

    private JPanel gameOptionPanel;
    private JButton singlePlayerButton, multiPlayerButton, backToMenuButton;

    private JPanel singlePlayPanel;
    private JButton soloPlayButton, challengeModeButton, rankingButton, backToOptionButton;

    private JPanel recordPanel;
    private JLabel mineCountLabel, tryLabel, successLabel, failLabel;

    private JPanel soloPlayPanel, widthSliderPanel, mineCountPanel;
    private JSlider widthSlider, mineCountSlider;
    private JLabel widthLabel, mineLabel;
    private JButton startButton;

    private JPanel gamePanel;
    public JButton[] mineButtons;

    private JPanel challengeMenuPanel;
    private JButton easyModeButton, normalModeButton, hardModeButton, backToSinglePlayMenuButton;

    private JPanel rankingMenuPanel;
    private JButton easyRankingButton, normalRankingButton, hardRankingButton, backToChallengeMenuButton;
    private JPanel multiPlayMenuPanel;
    private JButton lobbyButton, backToOptionButton2;
    private JPanel lobbyPanel, onlinePlayerDisplay, chattingPanel, chattingTopPanel, chattingBottomPanel;
    private JButton exitLobbyButton, sendButton, readyButton;
    private JButton[] onlinePlayerButtons;
    private JScrollPane chattingTextScrollPane;
    private JTextField chatTextField;
    private JTextArea chattingTextArea;

    private JFrame interactionFrame;
    private Container interactionCont;
    private JPanel interactionPanel;
    private JButton whisperButton, infoButton;

    public static void main(String[] args) throws Exception {
        ClientGUI clientGUI = new ClientGUI();
        clientGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // WindowListener 를 사용하여 X 버튼 클릭을 무시
        clientGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 창을 닫지 않고 메시지를 표시
                JOptionPane.showMessageDialog(clientGUI, "종료를 원할 시 종료 버튼을 눌러주세요.", "종료", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 최대화 버튼 비활성화
        clientGUI.setResizable(false);
    }

    public ClientGUI() throws Exception {
        setupUI();
        connectToServer();
        setResizable(false); // 화면 크기 조정 비활성화
    }

    private void connectToServer() throws IOException {
        Socket socket = new Socket(address, inPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    private void setupUI() {
        setTitle("Client");
        setSize(400, 300);
        setLocationRelativeTo(null);  // Center the window

        setLayout(new BorderLayout());

        // 스타일 및 디자인 설정
        UIManager.put("Label.font", new Font("나눔고딕", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("나눔고딕", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("나눔고딕", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("나눔고딕", Font.PLAIN, 14));

        Color backgroundColor = new Color(60, 63, 65);
        Color panelColor = new Color(43, 43, 43);
        Color buttonColor = new Color(75, 110, 175);
        Color textColor = new Color(187, 187, 187);

        getContentPane().setBackground(backgroundColor);

        // 로그인 패널 설정
        loginPanel = createLoginPanel(panelColor, buttonColor, textColor);
        add(loginPanel, BorderLayout.CENTER);

        // 게임 메뉴 패널 설정
        gameMenuPanel = createGameMenuPanel(panelColor, buttonColor);

        // 게임 옵션 패널 설정
        gameOptionPanel = createGameOptionPanel(panelColor, buttonColor);

        // 싱글 플레이 패널 설정
        singlePlayPanel = createSinglePlayPanel(panelColor, buttonColor);

        // 혼자 놀기 맵 설정 패널
        soloPlayPanel = createSoloPlayPanel(panelColor, buttonColor, textColor);

        // 게임 중 기록 패널
        recordPanel = createRecordPanel(panelColor, textColor);

        // 기록도전 메뉴 패널
        challengeMenuPanel = createChallengeMenuPanel(panelColor, buttonColor);

        // 싱글플레이 순위표 메뉴 패널
        rankingMenuPanel = createRankingMenuPanel(panelColor, buttonColor);

        // 멀티 플레이 패널 설정
        multiPlayMenuPanel = createMultiPlayMenuPanel(panelColor, buttonColor);

        // 멀티플레이 로비 패널 설정
        lobbyPanel = createLobbyPanel(panelColor, buttonColor, textColor);

        // 로그아웃, 종료 버튼 설정 (항상 고정)
        controlPanel = createControlPanel(panelColor, buttonColor);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createLoginPanel(Color panelColor, Color buttonColor, Color textColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        idField = new JTextField(15);
        passwordField = new JPasswordField(15);
        logInButton = new JButton("로그인");
        signUpButton = new JButton("회원가입");

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setForeground(textColor);
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setForeground(textColor);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(logInButton, gbc);
        gbc.gridy = 3;
        panel.add(signUpButton, gbc);

        logInButton.setBackground(buttonColor);
        logInButton.setForeground(Color.WHITE);
        signUpButton.setBackground(buttonColor);
        signUpButton.setForeground(Color.WHITE);

        logInButton.addActionListener(new LoginActionListener());
        signUpButton.addActionListener(new LoginActionListener());

        return panel;
    }

    private JPanel createGameMenuPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        gameButton = new JButton("지뢰찾기");
        gameButton.setBackground(buttonColor);
        gameButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가
        panel.add(gameButton, gbc);

        gameButton.addActionListener(new GameMenuActionListener());
        return panel;
    }

    private JPanel createGameOptionPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        singlePlayerButton = new JButton("싱글플레이");
        multiPlayerButton = new JButton("멀티플레이");
        backToMenuButton = new JButton("이전");

        singlePlayerButton.setBackground(buttonColor);
        singlePlayerButton.setForeground(Color.WHITE);
        multiPlayerButton.setBackground(buttonColor);
        multiPlayerButton.setForeground(Color.WHITE);
        backToMenuButton.setBackground(buttonColor);
        backToMenuButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        gbc.gridy = 0;
        panel.add(singlePlayerButton, gbc);
        gbc.gridy = 1;
        panel.add(multiPlayerButton, gbc);
        gbc.gridy = 2;
        panel.add(backToMenuButton, gbc);

        backToMenuButton.addActionListener(new GameOptionActionListener());
        singlePlayerButton.addActionListener(new GameOptionActionListener());
        multiPlayerButton.addActionListener(new GameOptionActionListener());

        return panel;
    }

    private JPanel createSinglePlayPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        soloPlayButton = new JButton("혼자놀기");
        challengeModeButton = new JButton("기록도전");
        rankingButton = new JButton("순위표");
        backToOptionButton = new JButton("이전");

        soloPlayButton.setBackground(buttonColor);
        soloPlayButton.setForeground(Color.WHITE);
        challengeModeButton.setBackground(buttonColor);
        challengeModeButton.setForeground(Color.WHITE);
        rankingButton.setBackground(buttonColor);
        rankingButton.setForeground(Color.WHITE);
        backToOptionButton.setBackground(buttonColor);
        backToOptionButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        gbc.gridy = 0;
        panel.add(soloPlayButton, gbc);
        gbc.gridy = 1;
        panel.add(challengeModeButton, gbc);
        gbc.gridy = 2;
        panel.add(rankingButton, gbc);
        gbc.gridy = 3;
        panel.add(backToOptionButton, gbc);

        soloPlayButton.addActionListener(new SinglePlayActionListener());
        challengeModeButton.addActionListener(new SinglePlayActionListener());
        rankingButton.addActionListener(new SinglePlayActionListener());
        backToOptionButton.addActionListener(new SinglePlayActionListener());

        return panel;
    }

    // 혼자 놀기 맵 설정 패널
    // 혼자 놀기 맵 설정 패널
    private JPanel createSoloPlayPanel(Color panelColor, Color buttonColor, Color textColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);

        // 맵 넓이 설정
        JLabel widthLabel = new JLabel("맵 넓이:");
        widthLabel.setForeground(textColor);
        JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        widthSlider.setMajorTickSpacing(1);
        widthSlider.setPaintTicks(true);
        widthSlider.setPaintLabels(true);
        widthSlider.setBackground(panelColor);
        widthSlider.setForeground(textColor);

        // 폭탄 개수 설정
        JLabel mineLabel = new JLabel("폭탄 개수:");
        mineLabel.setForeground(textColor);
        JSlider mineSlider = new JSlider(JSlider.HORIZONTAL, 0, 25, 10);
        mineSlider.setMajorTickSpacing(5);
        mineSlider.setMinorTickSpacing(1);
        mineSlider.setPaintTicks(true);
        mineSlider.setPaintLabels(true);
        mineSlider.setBackground(panelColor);
        mineSlider.setForeground(textColor);

        // 시작 버튼
        JButton startButton = new JButton("시작");
        startButton.setBackground(buttonColor);
        startButton.setForeground(Color.WHITE);

        // 슬라이더 이벤트 리스너 설정
        widthSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                width = widthSlider.getValue();
                mineSlider.setMaximum(width * width);
            }
        });

        mineSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mineCount = mineSlider.getValue();
            }
        });

        startButton.addActionListener(new CreateMapActionListener());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(widthLabel, gbc);
        gbc.gridx = 1;
        panel.add(widthSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(mineLabel, gbc);
        gbc.gridx = 1;
        panel.add(mineSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(startButton, gbc);

        return panel;
    }


    private JPanel createRecordPanel(Color panelColor, Color textColor) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBackground(panelColor);

        Font labelFont = new Font("나눔고딕", Font.BOLD, 16);
        Color labelColor = new Color(255, 255, 255);

        tryLabel = new JLabel();
        tryLabel.setFont(labelFont);
        tryLabel.setForeground(labelColor);
        successLabel = new JLabel();
        successLabel.setFont(labelFont);
        successLabel.setForeground(labelColor);
        failLabel = new JLabel();
        failLabel.setFont(labelFont);
        failLabel.setForeground(labelColor);
        mineCountLabel = new JLabel();
        mineCountLabel.setFont(labelFont);
        mineCountLabel.setForeground(labelColor);

        panel.add(mineCountLabel);
        panel.add(tryLabel);
        panel.add(successLabel);
        panel.add(failLabel);

        return panel;
    }

    private JPanel createChallengeMenuPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        easyModeButton = new JButton("쉬움");
        normalModeButton = new JButton("보통");
        hardModeButton = new JButton("어려움");
        backToSinglePlayMenuButton = new JButton("이전");

        easyModeButton.setBackground(buttonColor);
        easyModeButton.setForeground(Color.WHITE);
        normalModeButton.setBackground(buttonColor);
        normalModeButton.setForeground(Color.WHITE);
        hardModeButton.setBackground(buttonColor);
        hardModeButton.setForeground(Color.WHITE);
        backToSinglePlayMenuButton.setBackground(buttonColor);
        backToSinglePlayMenuButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        gbc.gridy = 0;
        panel.add(easyModeButton, gbc);
        gbc.gridy = 1;
        panel.add(normalModeButton, gbc);
        gbc.gridy = 2;
        panel.add(hardModeButton, gbc);
        gbc.gridy = 3;
        panel.add(backToSinglePlayMenuButton, gbc);

        easyModeButton.addActionListener(new ChallengeMenuActionListener());
        normalModeButton.addActionListener(new ChallengeMenuActionListener());
        hardModeButton.addActionListener(new ChallengeMenuActionListener());
        backToSinglePlayMenuButton.addActionListener(new ChallengeMenuActionListener());

        return panel;
    }

    private JPanel createRankingMenuPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        easyRankingButton = new JButton("쉬움 순위표");
        normalRankingButton = new JButton("보통 순위표");
        hardRankingButton = new JButton("어려움 순위표");
        backToChallengeMenuButton = new JButton("이전");

        easyRankingButton.setBackground(buttonColor);
        easyRankingButton.setForeground(Color.WHITE);
        normalRankingButton.setBackground(buttonColor);
        normalRankingButton.setForeground(Color.WHITE);
        hardRankingButton.setBackground(buttonColor);
        hardRankingButton.setForeground(Color.WHITE);
        backToChallengeMenuButton.setBackground(buttonColor);
        backToChallengeMenuButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        gbc.gridy = 0;
        panel.add(easyRankingButton, gbc);
        gbc.gridy = 1;
        panel.add(normalRankingButton, gbc);
        gbc.gridy = 2;
        panel.add(hardRankingButton, gbc);
        gbc.gridy = 3;
        panel.add(backToChallengeMenuButton, gbc);

        easyRankingButton.addActionListener(new RankingMenuActionListener());
        normalRankingButton.addActionListener(new RankingMenuActionListener());
        hardRankingButton.addActionListener(new RankingMenuActionListener());
        backToChallengeMenuButton.addActionListener(new RankingMenuActionListener());

        return panel;
    }

    private JPanel createMultiPlayMenuPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);
        lobbyButton = new JButton("로비");
        backToOptionButton2 = new JButton("이전");

        lobbyButton.setBackground(buttonColor);
        lobbyButton.setForeground(Color.WHITE);
        backToOptionButton2.setBackground(buttonColor);
        backToOptionButton2.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        gbc.gridy = 0;
        panel.add(lobbyButton, gbc);
        gbc.gridy = 1;
        panel.add(backToOptionButton2, gbc);

        lobbyButton.addActionListener(new MultiPlayActionListener());
        backToOptionButton2.addActionListener(new MultiPlayActionListener());

        return panel;
    }

    private JPanel createLobbyPanel(Color panelColor, Color buttonColor, Color textColor) {
        chattingBottomPanel = new JPanel(new BorderLayout());
        chattingBottomPanel.setBackground(panelColor);
        chatTextField = new JTextField("메시지를 입력하세요");
        chatTextField.setPreferredSize(new Dimension(200, 24));
        sendButton = new JButton("전송");
        readyButton = new JButton("준비");
        chattingBottomPanel.add(readyButton, BorderLayout.WEST);
        chattingBottomPanel.add(chatTextField, BorderLayout.CENTER);
        chattingBottomPanel.add(sendButton, BorderLayout.EAST);

        chatTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                chatTextField.setText("");
            }
        });

        chattingTextArea = new JTextArea();
        chattingTextArea.setEditable(false);
        chattingTextArea.setBackground(panelColor);
        chattingTextArea.setForeground(textColor);

        chattingTextScrollPane = new JScrollPane(chattingTextArea);
        chattingTextScrollPane.setPreferredSize(new Dimension(300, 400));

        chattingTopPanel = new JPanel(new BorderLayout());
        chattingTopPanel.setBackground(panelColor);
        exitLobbyButton = new JButton("나가기");
        chattingTopPanel.add(exitLobbyButton, BorderLayout.WEST);

        chattingPanel = new JPanel(new BorderLayout());
        chattingPanel.setBackground(panelColor);
        chattingPanel.add(chattingTopPanel, BorderLayout.NORTH);
        chattingPanel.add(chattingTextScrollPane, BorderLayout.CENTER);
        chattingPanel.add(chattingBottomPanel, BorderLayout.SOUTH);

        exitLobbyButton.addActionListener(new LobbyActionListener());
        sendButton.addActionListener(new LobbyActionListener());
        readyButton.addActionListener(new LobbyActionListener());

        onlinePlayerDisplay = new JPanel(new GridLayout(3, 3));
        onlinePlayerDisplay.setBackground(panelColor);
        onlinePlayerButtons = new JButton[9];
        for (int i = 0; i < 9; i++) {
            onlinePlayerButtons[i] = new JButton();
            onlinePlayerButtons[i].addActionListener(new OnlinePlayerActionListener());
            onlinePlayerButtons[i].setBackground(buttonColor);
            onlinePlayerButtons[i].setForeground(Color.WHITE);
            onlinePlayerDisplay.add(onlinePlayerButtons[i]);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        panel.add(chattingPanel, BorderLayout.CENTER);
        panel.add(onlinePlayerDisplay, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel(Color panelColor, Color buttonColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(panelColor);
        exitButton = new JButton("종료");
        logOutButton = new JButton("로그아웃");

        exitButton.setBackground(buttonColor);
        exitButton.setForeground(Color.WHITE);
        logOutButton.setBackground(buttonColor);
        logOutButton.setForeground(Color.WHITE);

        logOutButton.setEnabled(false); // 로그인 전 로그아웃 버튼 비활성화
        panel.add(exitButton, BorderLayout.WEST);
        panel.add(logOutButton, BorderLayout.EAST);

        exitButton.addActionListener(new ControlActionListener());
        logOutButton.addActionListener(new ControlActionListener());

        return panel;
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                loginOption(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class GameMenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                gameMenu(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class GameOptionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                gameOption(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class SinglePlayActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                singlePlayOption(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class ChallengeMenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            challengeOption(e.getActionCommand());
        }
    }

    private class RankingMenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                rankingOption(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class MultiPlayActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                multiPlayOption(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class LobbyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            lobbyOption(e.getActionCommand());
        }
    }

    private class OnlinePlayerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            onlinePlayerOption(e.getActionCommand());
        }
    }

    private class ControlActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                controlOption(e.getActionCommand());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class MapSettingListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();
            if (s == widthSlider) {
                width = s.getValue();
                mineCountSlider.setMaximum(width * width);
                mineCountSlider.setMinorTickSpacing(1);
                mineCountSlider.setMajorTickSpacing(5);
                mineCountSlider.setPaintTicks(true);
                mineCountSlider.setPaintLabels(true);
            } else {
                mineCount = s.getValue();
            }
        }
    }

    private class CreateMapActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            createSingleMap();
        }
    }

    class MineFieldActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            int i = Integer.parseInt(s);
            int x = i / width;
            int y = i % width;

            try {
                out.println(x + "," + y);
                String msg = in.readLine();
                int result = Integer.parseInt(msg);
                JButton b = (JButton) e.getSource();
                if (result == 9) {
                    b.setText("\uD83D\uDCA3");
                    successCount++;
                    mineCount--;
                } else if (result == 10) {
                    JOptionPane.showMessageDialog(ClientGUI.this, "이미 발견한 폭탄입니다.", "경고", JOptionPane.ERROR_MESSAGE);
                    failCount++;
                } else {
                    b.setText(String.valueOf(result));
                    failCount++;
                }
                tryCount++;

                tryLabel.setText("시도: " + tryCount);
                successLabel.setText("성공: " + successCount);
                failLabel.setText("실패: " + failCount);
                mineCountLabel.setText("남은 폭탄: " + mineCount + "개");

                if (mineCount == 0) {
                    JOptionPane.showMessageDialog(ClientGUI.this, "폭탄찾기 완료!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    out.println(tryCount);
                    showGameOption();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    class MultiGameActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (isTurn) {
                isTurn = false;
                String s = e.getActionCommand();
                int i = Integer.parseInt(s);
                int x = i / width;
                int y = i % width;

                out.println(x + "," + y);

                tryLabel.setText("시도: " + tryCount);
                successLabel.setText("성공: " + successCount);
                failLabel.setText("실패: " + failCount);

            } else {
                JOptionPane.showMessageDialog(ClientGUI.this, "다른 플레이어들의 턴을 기다려주세요.", "대기", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void loginOption(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "로그인":
                login();
                break;
            case "회원가입":
                signUp();
                break;
        }
    }

    void gameMenu(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "지뢰찾기":
                showGameOption();
                break;
        }
    }

    void gameOption(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "싱글플레이":
                showSinglePlayOption();
                break;
            case "멀티플레이":
                showMultiPlayOption();
                break;
            case "이전":
                showGameMenu();
                break;
        }
    }

    void multiPlayOption(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "로비":
                lobby();
                break;
            case "이전":
                showGameOption();
                break;
        }
    }

    void lobbyOption(String option) {
        out.println(option);
        switch (option) {
            case "준비":
                break;
            case "전송":
                sendMessage();
                break;
            case "나가기":
                exitLobby();
                break;
        }
    }

    void onlinePlayerOption(String option) {
        if (option == null || option.trim().isEmpty()) {
            return;
        }

        out.println("상호작용");
        interactionFrame = new JFrame(option);
        interactionCont = interactionFrame.getContentPane();
        interactionPanel = new JPanel(new GridLayout(1, 2));
        interactionPanel.setBackground(new Color(43, 43, 43));
        whisperButton = new JButton("귓속말");
        infoButton = new JButton("회원정보");

        whisperButton.setBackground(new Color(75, 110, 175));
        whisperButton.setForeground(Color.WHITE);
        infoButton.setBackground(new Color(75, 110, 175));
        infoButton.setForeground(Color.WHITE);

        interactionPanel.add(whisperButton);
        interactionPanel.add(infoButton);

        interactionCont.add(interactionPanel);

        whisperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("귓속말");
                String msg = JOptionPane.showInputDialog(ClientGUI.this, option + "님에게 보낼 메세지를 입력하세요", "귓속말", JOptionPane.PLAIN_MESSAGE);
                out.println(option);
                out.println(msg);
                interactionFrame.dispose();
            }
        });

        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("회원정보");
                out.println(option);
                interactionFrame.dispose();
            }
        });

        interactionFrame.pack();
        interactionFrame.setLocationRelativeTo(this);
        interactionFrame.setVisible(true);
    }

    void singlePlayOption(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "혼자놀기":
                soloPlay();
                break;
            case "기록도전":
                showChallengeMenu();
                break;
            case "순위표":
                showRankingMenu();
                break;
            case "이전":
                showGameOption();
                break;
        }
    }

    void challengeOption(String option) {
        out.println(option);
        switch (option) {
            case "쉬움":
                width = 5;
                mineCount = 5;
                createSingleMap();
                break;
            case "보통":
                width = 8;
                mineCount = 8;
                createSingleMap();
                break;
            case "어려움":
                width = 13;
                mineCount = 13;
                createSingleMap();
                break;
            case "이전":
                showSinglePlayOption();
                break;
        }
    }

    void rankingOption(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "쉬움 순위표", "보통 순위표", "어려움 순위표":
                requestRanking();
                break;
            case "이전":
                showSinglePlayOption();
                break;
        }
    }

    void controlOption(String option) throws IOException {
        out.println(option);
        switch (option) {
            case "로그아웃":
                logOut();
                break;
            case "종료":
                exit();
                break;
        }
    }

    void login() throws IOException {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());

        out.println(id);
        String response = in.readLine();
        if (response.equals("아이디 존재")) {
            out.println(password);
            response = in.readLine();
            out.println("아이디 체크 완료");
            if (response.equals("비밀번호 일치")) {
                JOptionPane.showMessageDialog(ClientGUI.this, "로그인 완료", "로그인", JOptionPane.INFORMATION_MESSAGE);
                logOutButton.setEnabled(true);
                showGameMenu();
            } else {
                JOptionPane.showMessageDialog(ClientGUI.this, "비밀번호가 일치하지 않습니다.", "로그인", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(ClientGUI.this, "아이디가 존재하지 않습니다.", "로그인", JOptionPane.ERROR_MESSAGE);
        }
    }

    void signUp() throws IOException {
        String id = JOptionPane.showInputDialog(ClientGUI.this, "아이디를 입력하세요:", "회원가입", JOptionPane.QUESTION_MESSAGE);
        if (id == null) {
            out.println("취소");
            return;
        }
        out.println(id);
        while (in.readLine().equals("중복")) {
            JOptionPane.showMessageDialog(ClientGUI.this, "아이디가 이미 존재합니다.", "회원가입", JOptionPane.WARNING_MESSAGE);
            id = JOptionPane.showInputDialog(ClientGUI.this, "아이디를 입력하세요:", "회원가입", JOptionPane.QUESTION_MESSAGE);
            if (id == null) {
                out.println("취소");
                return;
            }
            out.println(id);
        }
        out.println("아이디 설정 완료");

        String name = JOptionPane.showInputDialog(ClientGUI.this, "이름을 입력하세요:", "회원가입", JOptionPane.QUESTION_MESSAGE);
        if (name == null) {
            out.println("취소");
            return;
        }

        String password = JOptionPane.showInputDialog(ClientGUI.this, "비밀번호를 입력하세요:", "회원가입", JOptionPane.QUESTION_MESSAGE);
        if (password == null) {
            out.println("취소");
            return;
        }

        String nickname = JOptionPane.showInputDialog(ClientGUI.this, "닉네임을 입력하세요:", "회원가입", JOptionPane.QUESTION_MESSAGE);
        if (nickname == null) {
            out.println("취소");
            return;
        }
        out.println(nickname);
        while (in.readLine().equals("중복")) {
            JOptionPane.showMessageDialog(ClientGUI.this, "닉네임이 이미 존재합니다.", "회원가입", JOptionPane.WARNING_MESSAGE);
            nickname = JOptionPane.showInputDialog(ClientGUI.this, "닉네임을 입력하세요:", "회원가입", JOptionPane.QUESTION_MESSAGE);
            if (nickname == null) {
                out.println("취소");
                return;
            }
            out.println(nickname);
        }
        out.println("정보 입력 완료");

        User user = new User(name, id, password, nickname);
        oos.writeObject(user);
        oos.flush();
        JOptionPane.showMessageDialog(ClientGUI.this, "회원가입 완료", "회원가입", JOptionPane.INFORMATION_MESSAGE);
    }

    void showLoginPanel() {
        getContentPane().removeAll();
        add(loginPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    void showGameMenu() {
        getContentPane().removeAll();
        add(gameMenuPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    void showGameOption() {
        getContentPane().removeAll();
        add(gameOptionPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    void showSinglePlayOption() {
        getContentPane().removeAll();
        add(singlePlayPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    void showSoloPlay() {
        getContentPane().removeAll();
        add(soloPlayPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
        pack();
    }

    void showChallengeMenu() {
        getContentPane().removeAll();
        add(challengeMenuPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    void showRankingMenu() {
        getContentPane().removeAll();
        add(rankingMenuPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    void showMultiPlayOption() {
        getContentPane().removeAll();
        add(multiPlayMenuPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        setSize(400, 300);
        repaint();
    }

    public void lobby() {
        getContentPane().removeAll();
        add(lobbyPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        revalidate();
        pack();
        repaint();

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String msg;
                while (!(msg = in.readLine()).equals("게임시작")) {
                    if (msg.equals("회원정보")) {
                        String name = in.readLine();
                        String id = in.readLine();
                        String nickname = in.readLine();
                        showUserInfo(name, id, nickname);
                    } else {
                        publish(msg); // publish 를 통해 UI 업데이트 요청
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                int exitPlayerIndex = 0;
                for (String message : chunks) {
                    if (message.contains("님이 로비에 참가했습니다.")) {
                        int index = message.indexOf("님이 로비에 참가했습니다.");
                        onlinePlayerButtons[onlinePlayerIndex].setText(message.substring(0, index).trim());
                        onlinePlayerButtons[onlinePlayerIndex].setForeground(Color.WHITE);
                        onlinePlayerIndex++;
                    } else if (message.contains("님이 로비를 떠났습니다.")) {
                        int index = message.indexOf("님이 로비를 떠났습니다.");
                        String text = message.substring(0, index).trim();
                        for (int i = 0; i < onlinePlayerIndex; i++) {
                            if (onlinePlayerButtons[i].getText().equals(text)) {
                                onlinePlayerButtons[i].setForeground(Color.BLACK);
                                exitPlayerIndex = i;
                            }
                        }
                        for (int i = exitPlayerIndex; i < onlinePlayerIndex - 1; i++) {
                            onlinePlayerButtons[i].setText(onlinePlayerButtons[i + 1].getText());
                            onlinePlayerButtons[i].setForeground(onlinePlayerButtons[i + 1].getForeground());
                        }
                        onlinePlayerButtons[onlinePlayerIndex - 1].setText("");
                        onlinePlayerButtons[onlinePlayerIndex - 1].setForeground(Color.BLACK);
                        onlinePlayerIndex--;
                    } else if (message.contains("님이 준비를 완료했습니다.")) {
                        int index = message.indexOf("님이 준비를 완료했습니다.");
                        String text = message.substring(0, index).trim();
                        for (int i = 0; i < onlinePlayerIndex; i++) {
                            if (onlinePlayerButtons[i].getText().equals(text)) {
                                onlinePlayerButtons[i].setForeground(Color.RED);
                            }
                        }
                    } else if (message.contains("님이 준비를 해제했습니다.")) {
                        int index = message.indexOf("님이 준비를 해제했습니다.");
                        String text = message.substring(0, index).trim();
                        for (int i = 0; i < onlinePlayerIndex; i++) {
                            if (onlinePlayerButtons[i].getText().equals(text)) {
                                onlinePlayerButtons[i].setForeground(Color.WHITE);
                            }
                        }
                    }
                    chattingLog(message); // UI 업데이트는 이벤트 디스패치 스레드에서 수행
                }
            }

            @Override
            protected void done() {
                try {
                    out.println("게임시작");
                    multiPlay();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        worker.execute();
    }

    void showUserInfo(String name, String id, String nickname) {
        JDialog infoDialog = new JDialog(this, "회원정보", true);
        infoDialog.setLayout(new GridLayout(4, 1));
        infoDialog.add(new JLabel("이름: " + name));
        infoDialog.add(new JLabel("아이디: " + id));
        infoDialog.add(new JLabel("닉네임: " + nickname));

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoDialog.dispose();
            }
        });

        infoDialog.add(closeButton);
        infoDialog.pack();
        infoDialog.setLocationRelativeTo(this);
        infoDialog.setVisible(true);
    }

    void clearLoginFields() {
        idField.setText("");
        passwordField.setText("");
    }

    void createSingleMap() {
        tryCount = 0;
        successCount = 0;
        failCount = 0;
        JOptionPane.showMessageDialog(ClientGUI.this, width + "x" + width + "맵, 폭탄개수: " + mineCount, "맵 설정", JOptionPane.INFORMATION_MESSAGE);
        out.println(width + "," + mineCount);

        tryLabel.setText("시도: " + tryCount);
        successLabel.setText("성공: " + successCount);
        failLabel.setText("실패: " + failCount);
        mineCountLabel.setText("남은 폭탄: " + mineCount + "개");

        gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(new Color(43, 43, 43));
        mineButtons = new JButton[width * width];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        for (int i = 0; i < width * width; i++) {
            mineButtons[i] = new JButton(); // 버튼을 빈 텍스트로 만들고
            mineButtons[i].setPreferredSize(new Dimension(50, 50)); // 버튼 크기 고정
            mineButtons[i].setBackground(Color.DARK_GRAY);
            mineButtons[i].setForeground(Color.RED);
            mineButtons[i].setFont(new Font("Apple Color Emoji", Font.PLAIN, 15));  // 이모티콘 폰트 설정

            mineButtons[i].setActionCommand("" + i); // 각 버튼에 고유 ActionCommand 설정
            mineButtons[i].addActionListener(new MineFieldActionListener());
            gbc.gridx = i % width;
            gbc.gridy = i / width;
            gamePanel.add(mineButtons[i], gbc);
        }

        getContentPane().removeAll();

        add(gamePanel, BorderLayout.CENTER);
        add(recordPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);
        pack();
        revalidate();
        repaint();
    }

    public void requestRanking() throws IOException {
        List<String[]> scores = new ArrayList<>();
        String line;
        while (!(line = in.readLine()).equals("END")) {
            scores.add(line.split(","));
        }
        displayRanking(scores);
    }

    private void displayRanking(List<String[]> scores) {
        getContentPane().removeAll();

        String[] columnNames = {"순위", "닉네임", "시도"};
        String[][] data = new String[scores.size()][3];
        for (int i = 0; i < scores.size(); i++) {
            data[i][0] = String.valueOf(i + 1); // Rank
            data[i][1] = scores.get(i)[0];     // Nickname
            data[i][2] = scores.get(i)[1];     // Score
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("이전");
        backButton.setBackground(new Color(75, 110, 175));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRankingMenu();
            }
        });
        JPanel backPanel = new JPanel();
        backPanel.setBackground(new Color(43, 43, 43));
        backPanel.add(backButton);
        add(backPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    public void sendMessage() {
        String msg = chatTextField.getText();
        out.println(msg);
        chatTextField.setText("메시지를 입력하세요");
    }

    public void chattingLog(String message) {
        chattingTextArea.append(message + "\n");
    }

    public void logOut() {
        JOptionPane.showMessageDialog(ClientGUI.this, "로그아웃 완료", "로그아웃", JOptionPane.INFORMATION_MESSAGE);
        chattingTextArea.setText("");
        onlinePlayerIndex = 0;
        logOutButton.setEnabled(false); // 로그아웃 버튼 비활성화
        clearLoginFields(); // 입력 필드 비우기
        showLoginPanel();
    }

    public void exit() {
        JOptionPane.showMessageDialog(ClientGUI.this, "종료합니다.", "종료", JOptionPane.INFORMATION_MESSAGE);
        try {
            in.close();
            out.close();
            oos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        dispose();
    }

    public void exitLobby() {
        // 나간 이후 로비 초기 세팅
        for (JButton onlinePlayerButton : onlinePlayerButtons) {
            onlinePlayerButton.setForeground(Color.BLACK);
            onlinePlayerButton.setText("");
        }
        onlinePlayerIndex = 0;
        chattingTextArea.setText("");
        showMultiPlayOption();
    }

    public void soloPlay() {
        showSoloPlay();
    }

    void createMultiMap() throws IOException {
        tryCount = 0;
        successCount = 0;
        failCount = 0;
        String msg = in.readLine();
        String[] arr = msg.split(",");
        width = Integer.parseInt(arr[0]);
        mineCount = Integer.parseInt(arr[1]);
        mineCountLabel.setText("남은 폭탄: " + mineCount + "개");

        JOptionPane.showMessageDialog(ClientGUI.this, "맵 넓이: " + width + ", 폭탄 수: " + mineCount + "생성", "맵 설정", JOptionPane.INFORMATION_MESSAGE);

        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(new Color(43, 43, 43));
        mineButtons = new JButton[width * width];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        for (int i = 0; i < width * width; i++) {
            mineButtons[i] = new JButton(); // 버튼을 빈 텍스트로 만들고
            mineButtons[i].setPreferredSize(new Dimension(50, 50)); // 버튼 크기 고정
            mineButtons[i].setBackground(Color.DARK_GRAY);
            mineButtons[i].setForeground(Color.RED);
            mineButtons[i].setFont(new Font("Apple Color Emoji", Font.PLAIN, 15));  // 이모티콘 폰트 설정

            mineButtons[i].setActionCommand("" + i); // 각 버튼에 고유 ActionCommand 설정
            mineButtons[i].addActionListener(new MultiGameActionListener());
            gbc.gridx = i % width;
            gbc.gridy = i / width;
            gamePanel.add(mineButtons[i], gbc);
        }

        getContentPane().removeAll();
        add(gamePanel, BorderLayout.CENTER);
        add(recordPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);
        pack();
        revalidate();
        repaint();
    }

    public void multiPlay() throws IOException {
        isTurn = true;
        createMultiMap();

        // 메시지 수신 스레드 시작
        MessageReceiver receiver = new MessageReceiver(in, this);
        receiver.start();
    }

    class MessageReceiver extends Thread {
        private BufferedReader in;
        private ClientGUI client;

        public MessageReceiver(BufferedReader in, ClientGUI client) {
            this.in = in;
            this.client = client;
        }

        public void run() {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equals("턴종료")) {
                        SwingUtilities.invokeLater(() -> {
                            client.isTurn = true;
                            JOptionPane.showMessageDialog(ClientGUI.this, "다음 턴을 시작할 수 있습니다.", "턴", JOptionPane.INFORMATION_MESSAGE);
                        });
                    } else if (msg.contains("님이 폭탄을 발견했습니다")) {
                        String[] parts = msg.split(",");
                        String info = parts[0];
                        int buttonIndex = Integer.parseInt(parts[1]);
                        mineButtons[buttonIndex].setForeground(Color.BLUE);
                        mineButtons[buttonIndex].setText("\uD83D\uDCA3");
                        JOptionPane.showMessageDialog(ClientGUI.this, info + "!", "알림", JOptionPane.INFORMATION_MESSAGE);
                        mineCount--;
                        mineCountLabel.setText("남은 폭탄: " + mineCount + "개");
                    } else if (msg.equals("게임종료")) {
                        out.println("끝");
                        JOptionPane.showMessageDialog(ClientGUI.this, "게임 종료!", "종료", JOptionPane.INFORMATION_MESSAGE);
                    } else if (msg.contains("순위")) {
                        StringBuilder rankingMsg = new StringBuilder();
                        String line;

                        while (!(line = in.readLine()).equals("END")) {
                            rankingMsg.append(line).append("\n");
                        }
                        JOptionPane.showMessageDialog(ClientGUI.this, rankingMsg.toString(), "게임 결과", JOptionPane.PLAIN_MESSAGE);

                        SwingUtilities.invokeLater(() -> {
                            client.exitLobby();
                        });
                        return;
                    } else {
                        String[] parts = msg.split(",");
                        int result = Integer.parseInt(parts[0]);
                        int buttonIndex = Integer.parseInt(parts[1]);

                        JButton button = mineButtons[buttonIndex];
                        if (result == 9) {
                            button.setText("\uD83D\uDCA3");
                            successCount++;
                            mineCount--;
                        } else if (result == 10) {
                            JOptionPane.showMessageDialog(ClientGUI.this, "이미 발견한 폭탄입니다.", "경고", JOptionPane.ERROR_MESSAGE);
                            failCount++;
                        } else {
                            button.setText(String.valueOf(result));
                            failCount++;
                        }
                        tryCount++;

                        tryLabel.setText("시도: " + tryCount);
                        successLabel.setText("성공: " + successCount);
                        failLabel.setText("실패: " + failCount);
                        mineCountLabel.setText("남은 폭탄: " + mineCount + "개");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
