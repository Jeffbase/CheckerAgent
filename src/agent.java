

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class agent {
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int MAX = 0;
    public static final int MIN = 1;

    public static class Piece {
        public int x;
        public int y;
        public int color; //white 0, black 1;
        public boolean king;

        Piece(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void move(int tox, int toy) {
            this.x = tox;
            this.y = toy;
        }

        Piece(Piece copyFrom) {
            this.x = copyFrom.x;
            this.y = copyFrom.y;
            this.king = copyFrom.king;
            this.color = copyFrom.color;
        }

        public void makeKing() {
            this.king = true;
        }
    }

    public static class Cell {
        public Piece p;

        Cell() {
            this.p = null;
        }

        Cell(Piece p) {
            this.p = p;
        }

        public boolean isEmpty() {
            return p == null;
        }
    }

    public static class Board {
        public Cell[][] board;
        public int current_player;
        public double score_white;
        public double score_black;
        public ArrayList<Piece> white_pieces;
        public ArrayList<Piece> black_pieces;

        Board(int player) {
            this.board = new Cell[8][8];
            this.current_player = player;
            this.black_pieces = new ArrayList<>();
            this.white_pieces = new ArrayList<>();
            this.score_black = 0;// TODO change score
            this.score_white = 0;

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    board[row][col] = new Cell();
                }
            }

            for (int row = 0; row < 8; row++) {
                for (int col = row % 2; col < 8; col = col + 2) {
                    if (row < 3) {
                        this.board[row][col].p = new Piece(row, col, WHITE);
                        this.white_pieces.add(this.board[row][col].p);
                    } else if (row >= 5) {
                        this.board[row][col].p = new Piece(row, col, BLACK);
                        this.black_pieces.add(this.board[row][col].p);
                    }
                }
            }
        }

        Board(List<String> preBoard, int player) { //TODO add evaluate function
            this.board = new Cell[8][8];
            this.black_pieces = new ArrayList<>();
            this.white_pieces = new ArrayList<>();
            this.current_player = player;


            for (int row = 7; row >= 0; row--) {
                for (int col = 0; col < 8; col++) {
                    board[row][col] = new Cell();
                    if (preBoard.get(7 - row).charAt(col) == 'b') {
                        board[row][col].p = new Piece(row, col, BLACK);
                        this.black_pieces.add(board[row][col].p);
                    } else if (preBoard.get(7 - row).charAt(col) == 'B') {
                        board[row][col].p = new Piece(row, col, BLACK);
                        board[row][col].p.makeKing();
                        this.black_pieces.add(board[row][col].p);
                    } else if (preBoard.get(7 - row).charAt(col) == 'w') {
                        board[row][col].p = new Piece(row, col, WHITE);
                        this.white_pieces.add(board[row][col].p);
                    } else if (preBoard.get(7 - row).charAt(col) == 'W') {
                        board[row][col].p = new Piece(row, col, WHITE);
                        board[row][col].p.makeKing();
                        this.white_pieces.add(board[row][col].p);
                    } else {
                        continue;
                    }
                }
            }
        }

        Board(Board copyFrom) {
            this.board = new Cell[8][8];
            this.current_player = copyFrom.current_player;
            this.black_pieces = new ArrayList<>();
            this.white_pieces = new ArrayList<>();
            this.score_white = copyFrom.score_white;
            this.score_black = copyFrom.score_black;

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    board[row][col] = new Cell();
                    if (!copyFrom.board[row][col].isEmpty()) {
                        this.board[row][col].p = new Piece(copyFrom.board[row][col].p);
                        if (board[row][col].p.color == WHITE) {
                            white_pieces.add(board[row][col].p);
                        } else {
                            black_pieces.add(board[row][col].p);
                        }
                    }
                }
            }
        }

        public void flipPlayer() {
            if (current_player == WHITE) {
                current_player = BLACK;
            } else
                current_player = WHITE;
        }

        public List validMove() {
            ArrayList<Piece> jumplist = new ArrayList<>();
            ArrayList<Piece> movelist = new ArrayList<>();
            if (current_player == WHITE) {
                for (Piece white_piece : white_pieces) {
                    int row = white_piece.x;
                    int col = white_piece.y;
                    if (!board[row][col].p.king &&
                            ((row + 1 < 8 && col - 1 >= 0 && !board[row + 1][col - 1].isEmpty() && board[row + 1][col - 1].p.color == BLACK)
                                    && (row + 2 < 8 && col - 2 >= 0 && board[row + 2][col - 2].isEmpty())
                                    || (row + 1 < 8 && col + 1 < 8 && !board[row + 1][col + 1].isEmpty() && board[row + 1][col + 1].p.color == BLACK)
                                    && (row + 2 < 8 && col + 2 < 8 && board[row + 2][col + 2].isEmpty()))) {
                        jumplist.add(board[row][col].p);
                    } else if (board[row][col].p.king &&
                            ((row + 1 < 8 && col - 1 >= 0 && !board[row + 1][col - 1].isEmpty() && board[row + 1][col - 1].p.color == BLACK)
                                    && (row + 2 < 8 && col - 2 >= 0 && board[row + 2][col - 2].isEmpty())
                                    || (row + 1 < 8 && col + 1 < 8 && !board[row + 1][col + 1].isEmpty() && board[row + 1][col + 1].p.color == BLACK)
                                    && (row + 2 < 8 && col + 2 < 8 && board[row + 2][col + 2].isEmpty())
                                    || (row - 1 >= 0 && col - 1 >= 0 && !board[row - 1][col - 1].isEmpty() && board[row - 1][col - 1].p.color == BLACK)
                                    && (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].isEmpty())
                                    || (row - 1 >= 0 && col + 1 < 8 && !board[row - 1][col + 1].isEmpty() && board[row - 1][col + 1].p.color == BLACK)
                                    && (row - 2 >= 0 && col + 2 < 8 && board[row - 2][col + 2].isEmpty()))) {
                        jumplist.add(board[row][col].p);
                    } else if (!board[row][col].p.king &&
                            ((row + 1 < 8 && col - 1 >= 0 && board[row + 1][col - 1].isEmpty())
                                    || (row + 1 < 8 && col + 1 < 8 && board[row + 1][col + 1].isEmpty()))) {
                        movelist.add(board[row][col].p);
                    } else if (board[row][col].p.king &&
                            ((row + 1 < 8 && col - 1 >= 0 && board[row + 1][col - 1].isEmpty())
                                    || (row + 1 < 8 && col + 1 < 8 && board[row + 1][col + 1].isEmpty())
                                    || (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].isEmpty())
                                    || (row - 1 >= 0 && col + 1 < 8 && board[row - 1][col + 1].isEmpty()))) {
                        movelist.add(board[row][col].p);
                    } else
                        continue;
                }
            } else {
                for (Piece black_piece : black_pieces) {
                    int row = black_piece.x;
                    int col = black_piece.y;
                    if (!board[row][col].p.king &&
                            ((row - 1 >= 0 && col - 1 >= 0 && !board[row - 1][col - 1].isEmpty() && board[row - 1][col - 1].p.color == WHITE)
                                    && (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].isEmpty())
                                    || (row - 1 >= 0 && col + 1 < 8 && !board[row - 1][col + 1].isEmpty() && board[row - 1][col + 1].p.color == WHITE)
                                    && (row - 2 >= 0 && col + 2 < 8 && board[row - 2][col + 2].isEmpty()))) {
                        jumplist.add(board[row][col].p);
                    } else if (board[row][col].p.king &&
                            ((row - 1 >= 0 && col - 1 >= 0 && !board[row - 1][col - 1].isEmpty() && board[row - 1][col - 1].p.color == WHITE)
                                    && (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].isEmpty())
                                    || (row - 1 >= 0 && col + 1 < 8 && !board[row - 1][col + 1].isEmpty() && board[row - 1][col + 1].p.color == WHITE)
                                    && (row - 2 >= 0 && col + 2 < 8 && board[row - 2][col + 2].isEmpty())
                                    || (row + 1 < 8 && col - 1 >= 0 && !board[row + 1][col - 1].isEmpty() && board[row + 1][col - 1].p.color == WHITE)
                                    && (row + 2 < 8 && col - 2 >= 0 && board[row + 2][col - 2].isEmpty())
                                    || (row + 1 < 8 && col + 1 < 8 && !board[row + 1][col + 1].isEmpty() && board[row + 1][col + 1].p.color == WHITE)
                                    && (row + 2 < 8 && col + 2 < 8 && board[row + 2][col + 2].isEmpty()))) {
                        jumplist.add(board[row][col].p);
                    } else if (!board[row][col].p.king &&
                            ((row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].isEmpty())
                                    || (row - 1 >= 0 && col + 1 < 8 && board[row - 1][col + 1].isEmpty()))) {
                        movelist.add(board[row][col].p);
                    } else if (board[row][col].p.king &&
                            ((row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].isEmpty())
                                    || (row - 1 >= 0 && col + 1 < 8 && board[row - 1][col + 1].isEmpty())
                                    || (row + 1 < 8 && col - 1 >= 0 && board[row + 1][col - 1].isEmpty())
                                    || (row + 1 < 8 && col + 1 < 8 && board[row + 1][col + 1].isEmpty()))) {
                        movelist.add(board[row][col].p);
                    } else
                        continue;
                }
            }
            if (!jumplist.isEmpty()) {
                return jumplist;
            } else
                return movelist;
        }

        public boolean isJumpable(int row, int col) {
            if (current_player == WHITE) {
                if (!board[row][col].p.king &&
                        ((row + 1 < 8 && col - 1 >= 0 && !board[row + 1][col - 1].isEmpty() && board[row + 1][col - 1].p.color == BLACK)
                                && (row + 2 < 8 && col - 2 >= 0 && board[row + 2][col - 2].isEmpty())
                                || (row + 1 < 8 && col + 1 < 8 && !board[row + 1][col + 1].isEmpty() && board[row + 1][col + 1].p.color == BLACK)
                                && (row + 2 < 8 && col + 2 < 8 && board[row + 2][col + 2].isEmpty()))) {
                    return true;
                } else return board[row][col].p.king &&
                        ((row + 1 < 8 && col - 1 >= 0 && !board[row + 1][col - 1].isEmpty() && board[row + 1][col - 1].p.color == BLACK)
                                && (row + 2 < 8 && col - 2 >= 0 && board[row + 2][col - 2].isEmpty())
                                || (row + 1 < 8 && col + 1 < 8 && !board[row + 1][col + 1].isEmpty() && board[row + 1][col + 1].p.color == BLACK)
                                && (row + 2 < 8 && col + 2 < 8 && board[row + 2][col + 2].isEmpty())
                                || (row - 1 >= 0 && col - 1 >= 0 && !board[row - 1][col - 1].isEmpty() && board[row - 1][col - 1].p.color == BLACK)
                                && (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].isEmpty())
                                || (row - 1 >= 0 && col + 1 < 8 && !board[row - 1][col + 1].isEmpty() && board[row - 1][col + 1].p.color == BLACK)
                                && (row - 2 >= 0 && col + 2 < 8 && board[row - 2][col + 2].isEmpty()));
            } else {
                if (!board[row][col].p.king &&
                        ((row - 1 >= 0 && col - 1 >= 0 && !board[row - 1][col - 1].isEmpty() && board[row - 1][col - 1].p.color == WHITE)
                                && (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].isEmpty())
                                || (row - 1 >= 0 && col + 1 < 8 && !board[row - 1][col + 1].isEmpty() && board[row - 1][col + 1].p.color == WHITE)
                                && (row - 2 >= 0 && col + 2 < 8 && board[row - 2][col + 2].isEmpty()))) {
                    return true;
                } else return board[row][col].p.king &&
                        ((row - 1 >= 0 && col - 1 >= 0 && !board[row - 1][col - 1].isEmpty() && board[row - 1][col - 1].p.color == WHITE)
                                && (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].isEmpty())
                                || (row - 1 >= 0 && col + 1 < 8 && !board[row - 1][col + 1].isEmpty() && board[row - 1][col + 1].p.color == WHITE)
                                && (row - 2 >= 0 && col + 2 < 8 && board[row - 2][col + 2].isEmpty())
                                || (row + 1 < 8 && col - 1 >= 0 && !board[row + 1][col - 1].isEmpty() && board[row + 1][col - 1].p.color == WHITE)
                                && (row + 2 < 8 && col - 2 >= 0 && board[row + 2][col - 2].isEmpty())
                                || (row + 1 < 8 && col + 1 < 8 && !board[row + 1][col + 1].isEmpty() && board[row + 1][col + 1].p.color == WHITE)
                                && (row + 2 < 8 && col + 2 < 8 && board[row + 2][col + 2].isEmpty()));
            }
        }

        public double scoreEvaluate() {
            //TODO evaluation function
            score_white = 0;
            score_black = 0;

            double size = white_pieces.size() + black_pieces.size();
            for (Piece white : white_pieces) {
                if (white.king) {
                    score_white += 15;
                } else {
                    score_white = score_white + 5 + white.x + 1;
                }
            }
            for (Piece black : black_pieces) {
                if (black.king) {
                    score_black += 15;
                } else {
                    score_black = score_black + 5 + (8 - black.x);
                }
            }

            return (score_white - score_black) ;
        }

        public double endGameEvaluate() {
            score_white = 0;
            score_black = 0;
            double size = white_pieces.size() + black_pieces.size();
            double distance = 0;
            for (Piece white : white_pieces) {
                for (Piece black : black_pieces) {
                    distance += distanceWB(white, black);
                }
            }
            distance = distance / size;
            if (white_pieces.size() > black_pieces.size()) {
                score_white = white_pieces.size() * 9 - 0.2 * distance;
                score_black = black_pieces.size() * 9 + 0.2 * distance;
            } else if (white_pieces.size() < black_pieces.size()) {
                score_white = white_pieces.size() * 9 + 0.2 * distance;
                score_black = black_pieces.size() * 9 - 0.2 * distance;
            } else {
                score_white = white_pieces.size() * 9;
                score_black = black_pieces.size() * 9;
            }
            return (score_white - score_black) ;
        }

        public double distanceWB(Piece a, Piece b) {
            return (double) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        }


        public void removePiece(int color, int x, int y) {
            if (color == WHITE && !white_pieces.isEmpty()) {
                for (int i = 0; i < white_pieces.size(); i++) {
                    if (white_pieces.get(i).x == x && white_pieces.get(i).y == y) {
                        white_pieces.remove(i);
                    }
                }
            } else if (color == BLACK && !black_pieces.isEmpty()) {
                for (int i = 0; i < black_pieces.size(); i++) {
                    if (black_pieces.get(i).x == x && black_pieces.get(i).y == y) {
                        black_pieces.remove(i);
                    }
                }
            } else {
                System.err.println("Cannot remove piece: list of piece is empty");
            }
        }


        public ArrayList<Board> move(int row, int col, List<StringBuilder> str) {
            ArrayList<Board> result = new ArrayList<>();
            if (current_player == WHITE) {
                if (row + 1 < 8 && col - 1 >= 0 && board[row + 1][col - 1].isEmpty()) {
                    Board copy = new Board(this);
                    copy.board[row + 1][col - 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row + 1][col - 1].p.move(row + 1, col - 1);
                    if (row + 1 == 7) {
                        copy.board[row + 1][col - 1].p.makeKing();
                    }
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col - 1 + 97)).append((char) (row + 1 + 49));
                    str.add(s);
                }
                if (row + 1 < 8 && col + 1 < 8 && board[row + 1][col + 1].isEmpty()) {
                    Board copy = new Board(this);
                    copy.board[row + 1][col + 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row + 1][col + 1].p.move(row + 1, col + 1);
                    if (row + 1 == 7) {
                        copy.board[row + 1][col + 1].p.makeKing();
                    }
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col + 1 + 97)).append((char) (row + 1 + 49));
                    str.add(s);
                }
                if (board[row][col].p.king && (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].isEmpty())) {
                    Board copy = new Board(this);
                    copy.board[row - 1][col - 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row - 1][col - 1].p.move(row - 1, col - 1);
                    if (row - 1 == 7) {
                        copy.board[row - 1][col - 1].p.makeKing();
                    }
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col - 1 + 97)).append((char) (row - 1 + 49));
                    str.add(s);
                }
                if (board[row][col].p.king && (row - 1 >= 0 && col + 1 < 8 && board[row - 1][col + 1].isEmpty())) {
                    Board copy = new Board(this);
                    copy.board[row - 1][col + 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row - 1][col + 1].p.move(row - 1, col + 1);
                    if (row - 1 == 7) {
                        copy.board[row - 1][col + 1].p.makeKing();
                    }
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col + 1 + 97)).append((char) (row - 1 + 49));
                    str.add(s);
                }
            } else {
                if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].isEmpty()) {
                    Board copy = new Board(this);
                    copy.board[row - 1][col - 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row - 1][col - 1].p.move(row - 1, col - 1);
                    if (row - 1 == 0) {
                        copy.board[row - 1][col - 1].p.makeKing();
                    }
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col - 1 + 97)).append((char) (row - 1 + 49));
                    str.add(s);
                }
                if (row - 1 >= 0 && col + 1 < 8 && board[row - 1][col + 1].isEmpty()) {
                    Board copy = new Board(this);
                    copy.board[row - 1][col + 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row - 1][col + 1].p.move(row - 1, col + 1);
                    if (row - 1 == 0) {
                        copy.board[row - 1][col + 1].p.makeKing();
                    }
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col + 1 + 97)).append((char) (row - 1 + 49));
                    str.add(s);
                }
                if (board[row][col].p.king && (row + 1 < 8 && col - 1 >= 0 && board[row + 1][col - 1].isEmpty())) {
                    Board copy = new Board(this);
                    copy.board[row + 1][col - 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row + 1][col - 1].p.move(row + 1, col - 1);
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col - 1 + 97)).append((char) (row + 1 + 49));
                    str.add(s);
                }
                if (board[row][col].p.king && (row + 1 < 8 && col + 1 < 8 && board[row + 1][col + 1].isEmpty())) {
                    Board copy = new Board(this);
                    copy.board[row + 1][col + 1].p = copy.board[row][col].p;
                    copy.board[row][col].p = null;
                    copy.board[row + 1][col + 1].p.move(row + 1, col + 1);
                    copy.flipPlayer();
                    result.add(copy);
                    StringBuilder s = new StringBuilder();
                    s.append("E ").append((char) (col + 97)).append((char) (row + 49)).append(" ").append((char) (col + 1 + 97)).append((char) (row + 1 + 49));
                    str.add(s);
                }
            }
            return result;
        }


        public ArrayList<Board> jump(int fromx, int fromy, ArrayList<StringBuilder> str) {
            ArrayList<Board> result = new ArrayList<>();
            StringBuilder s = new StringBuilder();
            jumpHelper(result, str, this, fromx, fromy, s);
            return result;
        }

        public void jumpHelper(ArrayList<Board> result, ArrayList<StringBuilder> str, Board current, int x,
                               int y, StringBuilder s) {
            if (!current.isJumpable(x, y)) {
                //check the king
                if (current.current_player == WHITE && x == 7) {
                    current.board[x][y].p.makeKing();
                } else if (current.current_player == BLACK && x == 0) {
                    current.board[x][y].p.makeKing();
                }
                //
                current.flipPlayer();
                str.add(new StringBuilder(s));
                result.add(current);
                return;
            }
            if (current.current_player == WHITE) {
                if ((x + 1 < 8 && y - 1 >= 0 && !current.board[x + 1][y - 1].isEmpty() && current.board[x + 1][y - 1].p.color == BLACK)
                        && (x + 2 < 8 && y - 2 >= 0 && current.board[x + 2][y - 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x + 2][y - 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x + 2][y - 2].p.move(x + 2, y - 2);
                    copy.board[x + 1][y - 1].p = null;
                    copy.removePiece(BLACK, x + 1, y - 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y - 2 + 97)).append((char) (x + 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x + 2, y - 2, s);
                    s.delete(s.length() - 8, s.length());
                }
                if ((x + 1 < 8 && y + 1 < 8 && !current.board[x + 1][y + 1].isEmpty() && current.board[x + 1][y + 1].p.color == BLACK)
                        && (x + 2 < 8 && y + 2 < 8 && current.board[x + 2][y + 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x + 2][y + 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x + 2][y + 2].p.move(x + 2, y + 2);
                    copy.board[x + 1][y + 1].p = null;
                    copy.removePiece(BLACK, x + 1, y + 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y + 2 + 97)).append((char) (x + 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x + 2, y + 2, s);
                    s.delete(s.length() - 8, s.length());
                }
                if (current.board[x][y].p.king && (x - 1 >= 0 && y - 1 >= 0 && !current.board[x - 1][y - 1].isEmpty() && current.board[x - 1][y - 1].p.color == BLACK)
                        && (x - 2 >= 0 && y - 2 >= 0 && current.board[x - 2][y - 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x - 2][y - 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x - 2][y - 2].p.move(x - 2, y - 2);
                    copy.board[x - 1][y - 1].p = null;
                    copy.removePiece(BLACK, x - 1, y - 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y - 2 + 97)).append((char) (x - 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x - 2, y - 2, s);
                    s.delete(s.length() - 8, s.length());
                }
                if (current.board[x][y].p.king && (x - 1 >= 0 && y + 1 < 8 && !current.board[x - 1][y + 1].isEmpty() && current.board[x - 1][y + 1].p.color == BLACK)
                        && (x - 2 >= 0 && y + 2 < 8 && current.board[x - 2][y + 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x - 2][y + 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x - 2][y + 2].p.move(x - 2, y + 2);
                    copy.board[x - 1][y + 1].p = null;
                    copy.removePiece(BLACK, x - 1, y + 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y + 2 + 97)).append((char) (x - 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x - 2, y + 2, s);
                    s.delete(s.length() - 8, s.length());
                }
            } else {
                if ((x - 1 >= 0 && y - 1 >= 0 && !current.board[x - 1][y - 1].isEmpty() && current.board[x - 1][y - 1].p.color == WHITE)
                        && (x - 2 >= 0 && y - 2 >= 0 && current.board[x - 2][y - 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x - 2][y - 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x - 2][y - 2].p.move(x - 2, y - 2);
                    copy.board[x - 1][y - 1].p = null;
                    copy.removePiece(WHITE, x - 1, y - 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y - 2 + 97)).append((char) (x - 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x - 2, y - 2, s);
                    s.delete(s.length() - 8, s.length());
                }
                if ((x - 1 >= 0 && y + 1 < 8 && !current.board[x - 1][y + 1].isEmpty() && current.board[x - 1][y + 1].p.color == WHITE)
                        && (x - 2 >= 0 && y + 2 < 8 && current.board[x - 2][y + 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x - 2][y + 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x - 2][y + 2].p.move(x - 2, y + 2);
                    copy.board[x - 1][y + 1].p = null;
                    copy.removePiece(WHITE, x - 1, y + 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y + 2 + 97)).append((char) (x - 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x - 2, y + 2, s);
                    s.delete(s.length() - 8, s.length());
                }
                if (current.board[x][y].p.king && (x + 1 < 8 && y - 1 >= 0 && !current.board[x + 1][y - 1].isEmpty() && current.board[x + 1][y - 1].p.color == WHITE)
                        && (x + 2 < 8 && y - 2 >= 0 && current.board[x + 2][y - 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x + 2][y - 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x + 2][y - 2].p.move(x + 2, y - 2);
                    copy.board[x + 1][y - 1].p = null;
                    copy.removePiece(WHITE, x + 1, y - 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y - 2 + 97)).append((char) (x + 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x + 2, y - 2, s);
                    s.delete(s.length() - 8, s.length());
                }
                if (current.board[x][y].p.king && (x + 1 < 8 && y + 1 < 8 && !current.board[x + 1][y + 1].isEmpty() && current.board[x + 1][y + 1].p.color == WHITE)
                        && (x + 2 < 8 && y + 2 < 8 && current.board[x + 2][y + 2].isEmpty())) {
                    Board copy = new Board(current);
                    copy.board[x + 2][y + 2].p = copy.board[x][y].p;
                    copy.board[x][y].p = null;
                    copy.board[x + 2][y + 2].p.move(x + 2, y + 2);
                    copy.board[x + 1][y + 1].p = null;
                    copy.removePiece(WHITE, x + 1, y + 1);
                    s.append("J ").append((char) (y + 97)).append((char) (x + 49)).append(" ").append((char) (y + 2 + 97)).append((char) (x + 2 + 49)).append("\n");
                    jumpHelper(result, str, copy, x + 2, y + 2, s);
                    s.delete(s.length() - 8, s.length());
                }
            }


        }

    }

    public String singleMove(Board current) {
        String result = "";
        List<Piece> pieceList = current.validMove();
        if (pieceList.isEmpty()) {
            return result;
        }
        int row = pieceList.get(0).x;
        int col = pieceList.get(0).y;
        if (current.isJumpable(row, col)) {
            ArrayList<StringBuilder> jumpInstruction = new ArrayList<>();
            ArrayList<Board> boardList = current.jump(row, col, jumpInstruction);
            result = jumpInstruction.get(0).toString();
        } else {
            ArrayList<StringBuilder> moveInstruction = new ArrayList<>();
            ArrayList<Board> boardList = current.move(row, col, moveInstruction);
            result = moveInstruction.get(0).toString();
        }
        return result;
    }

    public int indexOfHeuristic(List<Double> heuristics, int maxOrMin) {
        List<Integer> indexList = new ArrayList<>();
        if (maxOrMin == MAX) {
            double maxHeuristics = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < heuristics.size(); i++) {
                if (heuristics.get(i) >= maxHeuristics) {
                    maxHeuristics = heuristics.get(i);
                }
            }
            for (int i = 0; i < heuristics.size(); i++) {
                if (maxHeuristics == heuristics.get(i)) {
                    indexList.add(i);
                }
            }
            return indexList.get(ThreadLocalRandom.current().nextInt(0, indexList.size()));
        } else {
            double minHeuristics = Double.POSITIVE_INFINITY;
            for (int i = 0; i < heuristics.size(); i++) {
                if (heuristics.get(i) <= minHeuristics) {
                    minHeuristics = heuristics.get(i);
                }
            }
            for (int i = 0; i < heuristics.size(); i++) {
                if (minHeuristics == heuristics.get(i)) {
                    indexList.add(i);
                }
            }
            return indexList.get(ThreadLocalRandom.current().nextInt(0, indexList.size()));
        }
    }

    public boolean isEndGame(Board current) {
        int whiteKing = 0;
        int blackKing = 0;
        for (Piece white : current.white_pieces) {
            if (white.king) whiteKing++;
        }
        for (Piece black : current.black_pieces) {
            if (black.king) blackKing++;
        }
        if (current.white_pieces.size() == whiteKing && current.black_pieces.size() == blackKing)
            return true;
        else return false;
    }

    public String alphaBeta(Board current, int depth) {
        String result = new String();
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        List<Piece> validPieces = current.validMove();
        List<Double> heuristics = new ArrayList<>();
        ArrayList<StringBuilder> possibleMoves = new ArrayList<>();
        //ArrayList<Board> possibleBoards = new ArrayList<>(); //delete
        if (validPieces.isEmpty()) {
            return result = null;
        }
        boolean endGame = isEndGame(current);
        if (current.current_player == WHITE) {
            for (Piece validPiece : validPieces) {
                int row = validPiece.x;
                int col = validPiece.y;
                if (current.isJumpable(row, col)) {
                    ArrayList<Board> boardList = current.jump(row, col, possibleMoves);
                    //possibleBoards.addAll(boardList); //delete
                    for (Board board : boardList) {
                        heuristics.add(minFunction(board, alpha, beta, depth - 1, endGame));
                    }
                } else {
                    ArrayList<Board> boardList = current.move(row, col, possibleMoves);
                    //possibleBoards.addAll(boardList); //delete
                    for (Board board : boardList) {
                        heuristics.add(minFunction(board, alpha, beta, depth - 1, endGame));
                    }
                }
            }
            int index = indexOfHeuristic(heuristics, MAX);
            result = possibleMoves.get(index).toString();
            //fin[0] = possibleBoards.get(index); //delete
            System.out.println("score is:" + heuristics.get(index));

        } else {
            for (Piece validPiece : validPieces) {
                int row = validPiece.x;
                int col = validPiece.y;
                if (current.isJumpable(row, col)) {
                    ArrayList<Board> boardList = current.jump(row, col, possibleMoves);
                    //possibleBoards.addAll(boardList); //delete
                    for (Board board : boardList) {
                        heuristics.add(maxFunction(board, alpha, beta, depth - 1, endGame));
                    }
                } else {
                    ArrayList<Board> boardList = current.move(row, col, possibleMoves);
                    //possibleBoards.addAll(boardList); //delete
                    for (Board board : boardList) {
                        heuristics.add(maxFunction(board, alpha, beta, depth - 1, endGame));
                    }
                }
            }
            int index = indexOfHeuristic(heuristics, MIN);
            result = possibleMoves.get(index).toString();
            //fin[0] = possibleBoards.get(index); //delete
            System.out.println("score is:" + heuristics.get(index));
        }

        return result;
    }

    public double maxFunction(Board current, double alpha, double beta, int depth, boolean endGame) {
        if (depth == 0 && !endGame) {
            return current.scoreEvaluate();
        } else if (depth == 0 && endGame) {
            return current.endGameEvaluate();
        }
        double score = Double.NEGATIVE_INFINITY;
        List<Piece> validPieces = current.validMove();
        if (validPieces.isEmpty()) {
            return score;
        }
        for (Piece validPiece : validPieces) {
            int row = validPiece.x;
            int col = validPiece.y;
            if (current.isJumpable(row, col)) {
                ArrayList<StringBuilder> jumpInstruction = new ArrayList<>();
                ArrayList<Board> boardList = current.jump(row, col, jumpInstruction);
                for (Board board : boardList) {
                    score = Math.max(score, minFunction(board, alpha, beta, depth - 1, endGame));
                    if (score >= beta) return score;
                    alpha = Math.max(score, alpha);
                }
            } else {
                ArrayList<StringBuilder> moveInstruction = new ArrayList<>();
                ArrayList<Board> boardList = current.move(row, col, moveInstruction);
                for (Board board : boardList) {
                    score = Math.max(score, minFunction(board, alpha, beta, depth - 1, endGame));
                    if (score >= beta) return score;
                    alpha = Math.max(score, alpha);
                }
            }

        }

        return score;
    }

    public double minFunction(Board current, double alpha, double beta, int depth, boolean endGame) {
        if (depth == 0 && !endGame) {
            return current.scoreEvaluate();
        } else if (depth == 0 && endGame) {
            return current.endGameEvaluate();
        }
        double score = Double.POSITIVE_INFINITY;
        List<Piece> validPieces = current.validMove();
        if (validPieces.isEmpty()) {
            return score;
        }
        for (Piece validPiece : validPieces) {
            int row = validPiece.x;
            int col = validPiece.y;
            if (current.isJumpable(row, col)) {
                ArrayList<StringBuilder> jumpInstruction = new ArrayList<>();
                ArrayList<Board> boardList = current.jump(row, col, jumpInstruction);
                for (Board board : boardList) {
                    score = Math.min(score, maxFunction(board, alpha, beta, depth - 1, endGame));
                    if (score <= alpha) return score;
                    beta = Math.min(score, beta);
                }
            } else {
                ArrayList<StringBuilder> moveInstruction = new ArrayList<>();
                ArrayList<Board> boardList = current.move(row, col, moveInstruction);
                for (Board board : boardList) {
                    score = Math.min(score, maxFunction(board, alpha, beta, depth - 1, endGame));
                    if (score <= alpha) return score;
                    beta = Math.min(score, beta);
                }
            }
        }


        return score;
    }


    public static void main(String[] args) throws IOException {

        // write your code here
        boolean newFile = false;
        File playData = new File("playdata.txt");
        if (playData.createNewFile()) {
            newFile = true;
            System.out.println("File created: " + playData.getName());
        } else {
            System.out.println("File already exists.");
        }

        File myInput = new File("input.txt");
        Scanner myReader = new Scanner(myInput);
        String game_type = myReader.nextLine();
        String color = myReader.nextLine();
        String time = myReader.nextLine();
        agent a = new agent();
        int depth = 11;
        List<String> preBoard = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            preBoard.add(myReader.nextLine());
        }
        myReader.close();
        if (!newFile) {
            Scanner DataReader = new Scanner(playData);
            long lastRunningTime = Long.parseLong(DataReader.nextLine());
            depth = Integer.parseInt(DataReader.nextLine());

            long time_s = (long)Double.parseDouble(time);
            if(time_s * 1000 < 50 * lastRunningTime){
                depth--;
                if(depth <= 0){
                    depth = 1;
                }
            }
            DataReader.close();
        }


        int current_player = color.equals("WHITE") ? WHITE : BLACK;
        Board current = new Board(preBoard, current_player);


        //System.out.println(a.alphaBeta(current, 8));
        try {
            File myObj = new File("output.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("output.txt");
            long startTime = System.nanoTime();
            if (game_type.equals("SINGLE")) {
                myWriter.write(a.singleMove(current));
            } else {
                if (a.isEndGame(current)) {
                    if(depth > 5){
                        depth = 5;
                    }
                }
                myWriter.write(a.alphaBeta(current, depth));


            }
            long endTime = System.nanoTime();
            System.out.println("Execution time in milliseconds : " +
                    (endTime - startTime) / 1000000);
            FileWriter DataWriter = new FileWriter("playdata.txt");
            String t = Long.toString((endTime - startTime) / 1000000);
            System.out.print("record time: "+t);
            DataWriter.write( t + "\n" );
            DataWriter.write(Integer.toString(depth));
            myWriter.close();
            DataWriter.close();
            System.out.println("Successfully wrote to the file.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }






    }

}
