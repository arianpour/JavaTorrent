package torrent.download.peer;

import org.johnnei.utils.JMath;

public class Bitfield {

	private byte[] bitfield;
	
	public Bitfield() {
		this(0);
	}
	
	public Bitfield(int size) {
		bitfield = new byte[size];
	}
	
	/**
	 * Increases or decreased the bitfield size but it will preserve the old data
	 * @param size The new size to grow/shrink to
	 */
	public void setBitfieldSize(int size) {
		if(size != bitfield.length) {
			byte[] newBitfield = new byte[size];
			int maxSize = JMath.min(size, bitfield.length);
			for(int i = 0; i < maxSize; i++) {
				newBitfield[i] = bitfield[i];
			}
			bitfield = newBitfield;
		}
	}
	
	/**
	 * Override bitfield with a given one
	 * @param bitfield The new bitfield
	 */
	public void setBitfield(byte[] bitfield) {
		this.bitfield = bitfield;
	}
	
	/**
	 * Checks the bitfield if we have the given piece
	 * @param pieceIndex the piece to check
	 * @return True if we verified the hash of that piece, else false
	 */
	public boolean hasPiece(int pieceIndex) {
		int byteIndex = pieceIndex / 8;
		int bit = pieceIndex % 8;
		if(byteIndex < bitfield.length) {
			return ((bitfield[byteIndex] >> bit) & 1) == 1;
		} else {
			return false;
		}
	}
	/**
	 * Notify that we have the given piece<br/>
	 * This will update the bitfield to bitwise OR the bit to 1
	 * @param pieceIndex The piece to add
	 */
	public void havePiece(int pieceIndex) {
		havePiece(pieceIndex, false);
	}
	
	/**
	 * Notify that we have the given piece<br/>
	 * This will update the bitfield to bitwise OR the bit to 1
	 * @param pieceIndex The piece to add
	 * @param mayExpand If the bitfield may grow to fit the new have data
	 */
	public void havePiece(int pieceIndex, boolean mayExpand) {
		int byteIndex = pieceIndex / 8;
		int bit = pieceIndex % 8;
		if(bitfield.length < byteIndex) {
			if(mayExpand) {
				byte[] newBitfield = new byte[byteIndex + 1];
				for(int i = 0; i < bitfield.length; i++) {
					newBitfield[i] = bitfield[i];
				}
				this.bitfield = newBitfield;
			} else {
				return; //Prevent IndexOutOfRange
			}
		}
		bitfield[byteIndex] |= (1 << bit);
	}

	/**
	 * Goes through the bitfield and checks how many pieces the client has
	 * @return The amount of pieces the client has
	 */
	public int hasPieceCount() {
		int have = 0;
		for(int i = 0; i < bitfield.length; i++) {
			byte b = bitfield[i];
			for (int bit = 7; bit > 0; bit--) {
				if (((b >> bit) & 1) == 1) {
					++have;
				}
			}
		}
		return have;
	}
	
}
