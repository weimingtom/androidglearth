package org.earth.geom;

public class Vec3 implements Cloneable {
	public float x;
	public float y;
	public float z;

	public Vec3() {

	}

	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 subtract(Vec3 b) {
		this.x -= b.x;
		this.y -= b.y;
		this.z -= b.z;
		return this;
	}

	public Vec3 normalize() {
		return this.scale(1 / this.magnitude());
	}

	public Vec3 scale(float s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		return this;
	}

	private float magnitude() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z
				* this.z);
	}

	public static Vec3 cross(Vec3 a, Vec3 b) {
		return new Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y
				- a.y * b.x);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Vec3 invert() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	/**
	 * Returns the scalar product of vectors v0 and v1.
	 * 
	 * @param {goog.vec.ArrayType} v0 The first vector.
	 * @param {goog.vec.ArrayType} v1 The second vector.
	 * @return {number} The scalar product.
	 */
	public static float dot(Vec3 v0, Vec3 v1) {
		return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z;
	}

	public static Vec3 sum(Vec3 orig, Vec3 scale) {
		return new Vec3(orig.x + scale.x, orig.y + scale.y, orig.z + scale.z);
	}

	public static float distance(Vec3 a, Vec3 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = a.z - b.z;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
