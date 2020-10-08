/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.radixdlt.crypto;

import com.google.common.hash.HashCode;
import com.google.common.primitives.UnsignedBytes;
import com.radixdlt.SecurityCritical;
import com.radixdlt.SecurityCritical.SecurityKind;

import java.security.SecureRandom;
import java.util.Comparator;

@SecurityCritical(SecurityKind.HASHING)
public final class HashUtils {

	private static final Comparator<HashCode> hashComparator = new Comparator<HashCode>() {

		private final Comparator<byte[]> bytesComparator = UnsignedBytes.lexicographicalComparator();

		@Override
		public int compare(HashCode o1, HashCode o2) {
			return bytesComparator.compare(o1.asBytes(), o2.asBytes());
		}
	};

	private static final SecureRandom secureRandom = new SecureRandom();

	private static final HashHandler shaHashHandler = new SHAHashHandler();

	/**
	 * Returns a hash consisting of 32 zeros.
	 */
	public static HashCode zero256() {
		return zero(32);
	}

	/**
	 * Returns a hash consisting of `length` zeros.
	 */
	public static HashCode zero(int length) {
		return HashCode.fromBytes(new byte[length]);
	}

	/**
	 * Returns a random hash of length 32.
	 */
	public static HashCode random256() {
		return random(32);
	}

	/**
	 * Returns a random hash of specified length.
	 */
    public static HashCode random(int length) {
		byte[] randomBytes = new byte[length];
		secureRandom.nextBytes(randomBytes);
		return HashCode.fromBytes(shaHashHandler.hash256(randomBytes));
	}

	/**
	 * Hashes the supplied array, returning a cryptographically secure 256-bit hash.
	 *
	 * @param dataToBeHashed The data to hash
	 * @return The digest by applying the 256-bit/32-byte hash function
	 */
	public static HashCode sha256(byte[] dataToBeHashed)	{
		return sha256(dataToBeHashed, 0, dataToBeHashed.length);
	}

	/**
	 * Hashes the specified portion of the array, returning a cryptographically secure 256-bit hash.
	 *
	 * @param dataToBeHashed The data to hash
	 * @param offset The offset within the array to start hashing data
	 * @param length The number of bytes in the array to hash
	 * @return The digest by applying the 256-bit/32-byte hash function.
	 */
	public static HashCode sha256(byte[] dataToBeHashed, int offset, int length) {
		return HashCode.fromBytes(shaHashHandler.hash256(dataToBeHashed, offset, length));
	}

	/**
	 * Hashes the specified portion of the array, returning a cryptographically secure 512-bit hash.
	 *
	 * @param dataToBeHashed The data to hash
	 * @return The 512-bit/64-byte hash
	 */
	public static HashCode sha512(byte[] dataToBeHashed) {
		return HashCode.fromBytes(shaHashHandler.hash512(dataToBeHashed));
	}

	/**
	 * Compares two HashCode instances using the underlying byte array.

	 * @param fst The first object to be compared
	 * @param snd The second object to be compared
	 * @return A negative integer, zero, or a positive integer as the
	 *         first argument is less than, equal to, or greater than the
	 *         second.
	 */
	public static int compare(HashCode fst, HashCode snd) {
		return hashComparator.compare(fst, snd);
	}

	private HashUtils() {
		throw new UnsupportedOperationException();
	}
}
