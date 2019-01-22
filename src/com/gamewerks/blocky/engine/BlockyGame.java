package com.gamewerks.blocky.engine;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

import java.util.*; // Includes Random();

public class BlockyGame {
	private static final int LOCK_DELAY_LIMIT = 30;

	private Board board;
	private Piece activePiece;
	private Direction movement;

	private int lockCounter;
	private int pieceIndex; //For shuffle array

	public BlockyGame() {
		board = new Board();
		movement = Direction.NONE;
		lockCounter = 0;
		shuffle(PieceKind.ALL); //Shuffle at beginning of game
		trySpawnBlock();
	}

	/***
	 * shuffle takes an array and uses Fisher-Yates Shuffle to rearrange the
	 * order of the array
	 * 
	 * @param arr: an array of PieceKinds Help with random from
	 *            http://stackoverflow.com/questions/5887709/getting-random-
	 *            numbers-in-java
	 */
	public void shuffle(PieceKind[] arr) {
		Random rand = new Random();
		for (int i = 0; i < arr.length; i++) {
			int j = rand.nextInt(arr.length);
			PieceKind k = arr[i];
			arr[i] = arr[j];
			arr[j] = k;
		}
	}

	private void trySpawnBlock() {
		if (activePiece == null) {

			if (pieceIndex >= PieceKind.ALL.length) {
				shuffle(PieceKind.ALL);
				pieceIndex = 0;
			}
			activePiece = new Piece(PieceKind.ALL[pieceIndex], 
						new Position(4, Constants.BOARD_WIDTH / 2 - 2)); //New starting pos

			pieceIndex++;
			if (board.collides(activePiece)) {
				System.exit(0);
			}
		}
	}

	private void processMovement() {
		Position nextPos;
		switch (movement) {
		case NONE:
			nextPos = activePiece.getPosition();
			break;
		case LEFT:
			nextPos = activePiece.getPosition().add(0, -1);
			break;
		case RIGHT:
			nextPos = activePiece.getPosition().add(0, 1);
			break; // add break to RIGHT
		default:
			throw new IllegalStateException("Unrecognized direction: " + movement.name());
		}
		if (!board.collides(activePiece.getLayout(), nextPos)) {
			activePiece.moveTo(nextPos);
		}
	}

	private void processGravity() {
		Position nextPos = activePiece.getPosition().add(1, 0); // Block falls down
		if (!board.collides(activePiece.getLayout(), nextPos)) {
			lockCounter = 0;
			activePiece.moveTo(nextPos);
		} else {
			if (lockCounter < LOCK_DELAY_LIMIT) {
				lockCounter += 1;
			} else {
				board.addToWell(activePiece);

				lockCounter = 0;
				activePiece = null;

			}
		}
	}

	private void processClearedLines() {

		board.deleteRows(board.getCompletedRows());
	}

	public void step() {
		trySpawnBlock();
		processMovement(); // add processMovement to make things move

		processGravity();
		processClearedLines();
	}

	public boolean[][] getWell() {
		return board.getWell();
	}

	public Piece getActivePiece() {
		return activePiece;
	}

	public void setDirection(Direction movement) {
		this.movement = movement;
	}

	//Prevents rotating pieces from overlapping
	public void rotatePiece(boolean dir) {
		activePiece.rotate(dir);
		if (board.collides(activePiece)) {
			activePiece.rotate(!dir);
		}
	}
}
