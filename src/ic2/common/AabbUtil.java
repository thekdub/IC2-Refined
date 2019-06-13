package ic2.common;

import ic2.api.Direction;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Vec3D;

public class AabbUtil {
	public static Direction getIntersection(Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb, Vec3D vec3d2) {
		double d = vec3d1.c();
		Vec3D vec3d3 = Vec3D.a(vec3d1.a / d, vec3d1.b / d, vec3d1.c / d);
		Direction direction = intersects(vec3d, vec3d3, axisalignedbb);
		if (direction == null) {
			return null;
		}
		else {
			Vec3D vec3d4;
			if (vec3d3.a < 0.0D && vec3d3.b < 0.0D && vec3d3.c < 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.d, axisalignedbb.e, axisalignedbb.f);
			}
			else if (vec3d3.a < 0.0D && vec3d3.b < 0.0D && vec3d3.c >= 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.d, axisalignedbb.e, axisalignedbb.c);
			}
			else if (vec3d3.a < 0.0D && vec3d3.b >= 0.0D && vec3d3.c < 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.d, axisalignedbb.b, axisalignedbb.f);
			}
			else if (vec3d3.a < 0.0D && vec3d3.b >= 0.0D && vec3d3.c >= 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.d, axisalignedbb.b, axisalignedbb.c);
			}
			else if (vec3d3.a >= 0.0D && vec3d3.b < 0.0D && vec3d3.c < 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.a, axisalignedbb.e, axisalignedbb.f);
			}
			else if (vec3d3.a >= 0.0D && vec3d3.b < 0.0D && vec3d3.c >= 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.a, axisalignedbb.e, axisalignedbb.c);
			}
			else if (vec3d3.a >= 0.0D && vec3d3.b >= 0.0D && vec3d3.c < 0.0D) {
				vec3d4 = Vec3D.a(axisalignedbb.a, axisalignedbb.b, axisalignedbb.f);
			}
			else {
				vec3d4 = Vec3D.a(axisalignedbb.a, axisalignedbb.b, axisalignedbb.c);
			}

			Vec3D vec3d5 = null;
			switch (direction.ordinal() + 1) {
				case 1:
				case 2:
					vec3d5 = Vec3D.a(1.0D, 0.0D, 0.0D);
					break;
				case 3:
				case 4:
					vec3d5 = Vec3D.a(0.0D, 1.0D, 0.0D);
					break;
				case 5:
				case 6:
					vec3d5 = Vec3D.a(0.0D, 0.0D, 1.0D);
			}

			Vec3D vec3d6 = getIntersectionWithPlane(vec3d, vec3d3, vec3d4, vec3d5);
			vec3d2.a = vec3d6.a;
			vec3d2.b = vec3d6.b;
			vec3d2.c = vec3d6.c;
			return direction;
		}
	}

	public static Direction intersects(Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb) {
		double[] ad = getRay(vec3d, vec3d1);
		if (vec3d1.a < 0.0D && vec3d1.b < 0.0D && vec3d1.c < 0.0D) {
			if (vec3d.a < axisalignedbb.a) {
				return null;
			}
			else if (vec3d.b < axisalignedbb.b) {
				return null;
			}
			else if (vec3d.c < axisalignedbb.c) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EF, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EH, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DH, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DC, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.BC, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.BF, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.HG, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.FG, axisalignedbb)) < 0.0D) {
				return Direction.ZP;
			}
			else {
				return side(ad, getEdgeRay(Edge.CG, axisalignedbb)) < 0.0D ? Direction.YP : Direction.XP;
			}
		}
		else if (vec3d1.a < 0.0D && vec3d1.b < 0.0D && vec3d1.c >= 0.0D) {
			if (vec3d.a < axisalignedbb.a) {
				return null;
			}
			else if (vec3d.b < axisalignedbb.b) {
				return null;
			}
			else if (vec3d.c > axisalignedbb.f) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.HG, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DH, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AD, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AB, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.BF, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.FG, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DC, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.CG, axisalignedbb)) > 0.0D) {
				return Direction.XP;
			}
			else {
				return side(ad, getEdgeRay(Edge.BC, axisalignedbb)) < 0.0D ? Direction.YP : Direction.ZN;
			}
		}
		else if (vec3d1.a < 0.0D && vec3d1.b >= 0.0D && vec3d1.c < 0.0D) {
			if (vec3d.a < axisalignedbb.a) {
				return null;
			}
			else if (vec3d.b > axisalignedbb.e) {
				return null;
			}
			else if (vec3d.c < axisalignedbb.c) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.FG, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EF, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AE, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AD, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DC, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.CG, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EH, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.HG, axisalignedbb)) > 0.0D) {
				return Direction.ZP;
			}
			else {
				return side(ad, getEdgeRay(Edge.DH, axisalignedbb)) < 0.0D ? Direction.XP : Direction.YN;
			}
		}
		else if (vec3d1.a < 0.0D && vec3d1.b >= 0.0D && vec3d1.c >= 0.0D) {
			if (vec3d.a < axisalignedbb.a) {
				return null;
			}
			else if (vec3d.b > axisalignedbb.e) {
				return null;
			}
			else if (vec3d.c > axisalignedbb.f) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EH, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AE, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AB, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.BC, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.CG, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.HG, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AD, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.DH, axisalignedbb)) > 0.0D) {
				return Direction.YN;
			}
			else {
				return side(ad, getEdgeRay(Edge.DC, axisalignedbb)) < 0.0D ? Direction.ZN : Direction.XP;
			}
		}
		else if (vec3d1.a >= 0.0D && vec3d1.b < 0.0D && vec3d1.c < 0.0D) {
			if (vec3d.a > axisalignedbb.d) {
				return null;
			}
			else if (vec3d.b < axisalignedbb.b) {
				return null;
			}
			else if (vec3d.c < axisalignedbb.c) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AB, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AE, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EH, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.HG, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.CG, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.BC, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EF, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.BF, axisalignedbb)) < 0.0D) {
				return Direction.XN;
			}
			else {
				return side(ad, getEdgeRay(Edge.FG, axisalignedbb)) < 0.0D ? Direction.ZP : Direction.YP;
			}
		}
		else if (vec3d1.a >= 0.0D && vec3d1.b < 0.0D && vec3d1.c >= 0.0D) {
			if (vec3d.a > axisalignedbb.d) {
				return null;
			}
			else if (vec3d.b < axisalignedbb.b) {
				return null;
			}
			else if (vec3d.c > axisalignedbb.f) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DC, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AD, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AE, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.EF, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.FG, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.CG, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AB, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.BC, axisalignedbb)) > 0.0D) {
				return Direction.ZN;
			}
			else {
				return side(ad, getEdgeRay(Edge.BF, axisalignedbb)) < 0.0D ? Direction.XN : Direction.YP;
			}
		}
		else if (vec3d1.a >= 0.0D && vec3d1.b >= 0.0D && vec3d1.c < 0.0D) {
			if (vec3d.a > axisalignedbb.d) {
				return null;
			}
			else if (vec3d.b > axisalignedbb.e) {
				return null;
			}
			else if (vec3d.c < axisalignedbb.c) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.BF, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AB, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AD, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.DH, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.HG, axisalignedbb)) < 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.FG, axisalignedbb)) > 0.0D) {
				return null;
			}
			else if (side(ad, getEdgeRay(Edge.AE, axisalignedbb)) > 0.0D && side(ad, getEdgeRay(Edge.EF, axisalignedbb)) > 0.0D) {
				return Direction.XN;
			}
			else {
				return side(ad, getEdgeRay(Edge.EH, axisalignedbb)) < 0.0D ? Direction.YN : Direction.ZP;
			}
		}
		else if (vec3d.a > axisalignedbb.d) {
			return null;
		}
		else if (vec3d.b > axisalignedbb.e) {
			return null;
		}
		else if (vec3d.c > axisalignedbb.f) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.EF, axisalignedbb)) < 0.0D) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.EH, axisalignedbb)) > 0.0D) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.DH, axisalignedbb)) < 0.0D) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.DC, axisalignedbb)) > 0.0D) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.BC, axisalignedbb)) < 0.0D) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.BF, axisalignedbb)) > 0.0D) {
			return null;
		}
		else if (side(ad, getEdgeRay(Edge.AB, axisalignedbb)) < 0.0D && side(ad, getEdgeRay(Edge.AE, axisalignedbb)) > 0.0D) {
			return Direction.XN;
		}
		else {
			return side(ad, getEdgeRay(Edge.AD, axisalignedbb)) < 0.0D ? Direction.ZN : Direction.YN;
		}
	}

	private static double[] getRay(Vec3D vec3d, Vec3D vec3d1) {
		double[] ad = new double[]{vec3d.a * vec3d1.b - vec3d1.a * vec3d.b, vec3d.a * vec3d1.c - vec3d1.a * vec3d.c, -vec3d1.a, vec3d.b * vec3d1.c - vec3d1.b * vec3d.c, -vec3d1.c, vec3d1.b};
		return ad;
	}

	private static double[] getEdgeRay(Edge edge, AxisAlignedBB axisalignedbb) {
		switch (edge.ordinal() + 1) {
			case 1:
				return new double[]{-axisalignedbb.b, -axisalignedbb.c, -1.0D, 0.0D, 0.0D, 0.0D};
			case 2:
				return new double[]{axisalignedbb.a, 0.0D, 0.0D, -axisalignedbb.c, 0.0D, 1.0D};
			case 3:
				return new double[]{0.0D, axisalignedbb.a, 0.0D, axisalignedbb.b, -1.0D, 0.0D};
			case 4:
				return new double[]{axisalignedbb.d, 0.0D, 0.0D, -axisalignedbb.c, 0.0D, 1.0D};
			case 5:
				return new double[]{0.0D, axisalignedbb.d, 0.0D, axisalignedbb.b, -1.0D, 0.0D};
			case 6:
				return new double[]{-axisalignedbb.e, -axisalignedbb.c, -1.0D, 0.0D, 0.0D, 0.0D};
			case 7:
				return new double[]{0.0D, axisalignedbb.a, 0.0D, axisalignedbb.e, -1.0D, 0.0D};
			case 8:
				return new double[]{-axisalignedbb.b, -axisalignedbb.f, -1.0D, 0.0D, 0.0D, 0.0D};
			case 9:
				return new double[]{axisalignedbb.a, 0.0D, 0.0D, -axisalignedbb.f, 0.0D, 1.0D};
			case 10:
				return new double[]{0.0D, axisalignedbb.d, 0.0D, axisalignedbb.e, -1.0D, 0.0D};
			case 11:
				return new double[]{-axisalignedbb.e, -axisalignedbb.f, -1.0D, 0.0D, 0.0D, 0.0D};
			case 12:
				return new double[]{axisalignedbb.d, 0.0D, 0.0D, -axisalignedbb.f, 0.0D, 1.0D};
			default:
				return new double[0];
		}
	}

	private static double side(double[] ad, double[] ad1) {
		return ad[2] * ad1[3] + ad[5] * ad1[1] + ad[4] * ad1[0] + ad[1] * ad1[5] + ad[0] * ad1[4] + ad[3] * ad1[2];
	}

	private static Vec3D getIntersectionWithPlane(Vec3D vec3d, Vec3D vec3d1, Vec3D vec3d2, Vec3D vec3d3) {
		double d = getDistanceToPlane(vec3d, vec3d1, vec3d2, vec3d3);
		return Vec3D.a(vec3d.a + vec3d1.a * d, vec3d.b + vec3d1.b * d, vec3d.c + vec3d1.c * d);
	}

	private static double getDistanceToPlane(Vec3D vec3d, Vec3D vec3d1, Vec3D vec3d2, Vec3D vec3d3) {
		Vec3D vec3d4 = Vec3D.a(vec3d2.a - vec3d.a, vec3d2.b - vec3d.b, vec3d2.c - vec3d.c);
		return dotProduct(vec3d4, vec3d3) / dotProduct(vec3d1, vec3d3);
	}

	private static double dotProduct(Vec3D vec3d, Vec3D vec3d1) {
		return vec3d.a * vec3d1.a + vec3d.b * vec3d1.b + vec3d.c * vec3d1.c;
	}

	enum Edge {
		AD,
		AB,
		AE,
		DC,
		DH,
		BC,
		BF,
		EH,
		EF,
		CG,
		FG,
		HG
	}
}
