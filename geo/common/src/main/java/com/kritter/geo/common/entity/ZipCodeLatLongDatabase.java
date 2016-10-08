package com.kritter.geo.common.entity;

import lombok.Getter;
import java.util.*;

public class ZipCodeLatLongDatabase<T extends ZipCodeLatLongDatabase.ZipCodeInfo>
{
    private int k = 3;
    private KdNode root = null;
    private static final int EARTH_RADIUS_KMS = 6371;

    private static final Comparator<ZipCodeInfo> LATITUDE_COMPARATOR = new Comparator<ZipCodeInfo>()
    {
        @Override
        public int compare(ZipCodeInfo o1, ZipCodeInfo o2)
        {
            if (o1.latitude < o2.latitude)
                return -1;
            if (o1.latitude > o2.latitude)
                return 1;
            return 0;
        }
    };

    private static final Comparator<ZipCodeInfo> LONGITUDE_COMPARATOR = new Comparator<ZipCodeInfo>()
    {
        @Override
        public int compare(ZipCodeInfo o1, ZipCodeInfo o2)
        {
            if (o1.longitude < o2.longitude)
                return -1;
            if (o1.longitude > o2.longitude)
                return 1;
            return 0;
        }
    };

    protected static final int X_AXIS = 0;
    protected static final int Y_AXIS = 1;

    /**
     * Default constructor.
     */
    public ZipCodeLatLongDatabase()
    {
    }

    /**
     * Constructor for creating a more balanced tree. It uses the
     * "median of points" algorithm.
     *
     * @param list
     *            of ZipCodeInfo.
     */
    public ZipCodeLatLongDatabase(List<ZipCodeInfo> list)
    {
        root = createNode(list, k, 0);
    }

    /**
     * Create node from list of ZipCodeInfo.
     *
     * @param list
     *            of ZipCodeInfo.
     * @param k
     *            of the tree.
     * @param depth
     *            depth of the node.
     * @return node created.
     */
    private static KdNode createNode(List<ZipCodeInfo> list, int k, int depth)
    {
        if (list == null || list.size() == 0)
            return null;

        int axis = depth % k;
        if (axis == X_AXIS)
            Collections.sort(list, LATITUDE_COMPARATOR);
        else if (axis == Y_AXIS)
            Collections.sort(list, LONGITUDE_COMPARATOR);

        KdNode node = null;
        if (list.size() > 0)
        {
            int medianIndex = list.size() / 2;
            node = new KdNode(k, depth, list.get(medianIndex));
            List<ZipCodeInfo> less = new ArrayList<ZipCodeInfo>(list.size() - 1);
            List<ZipCodeInfo> more = new ArrayList<ZipCodeInfo>(list.size() - 1);
            // Process list to see where each non-median point lies
            for (int i = 0; i < list.size(); i++)
            {
                if (i == medianIndex)
                    continue;
                ZipCodeInfo p = list.get(i);
                if (KdNode.compareTo(depth, k, p, node.id) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }
            if ((medianIndex - 1) >= 0)
            {
                if (less.size() > 0)
                {
                    node.lesser = createNode(less, k, depth + 1);
                    node.lesser.parent = node;
                }
            }
            if ((medianIndex + 1) <= (list.size() - 1))
            {
                if (more.size() > 0) {
                    node.greater = createNode(more, k, depth + 1);
                    node.greater.parent = node;
                }
            }
        }

        return node;
    }

    /**
     * Add value to the tree. Tree can contain multiple equal values.
     *
     * @param value
     *            T to add to the tree.
     * @return True if successfully added to tree.
     */
    public boolean add(T value) {
        if (value == null)
            return false;

        if (root == null) {
            root = new KdNode(value);
            return true;
        }

        KdNode node = root;
        while (true) {
            if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    KdNode newNode = new KdNode(k, node.depth + 1, value);
                    newNode.parent = node;
                    node.lesser = newNode;
                    break;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    KdNode newNode = new KdNode(k, node.depth + 1, value);
                    newNode.parent = node;
                    node.greater = newNode;
                    break;
                }
                node = node.greater;
            }
        }

        return true;
    }

    /**
     * Does the tree contain the value.
     *
     * @param value
     *            T to locate in the tree.
     * @return True if tree contains value.
     */
    public boolean contains(T value) {
        if (value == null)
            return false;

        KdNode node = getNode(this, value);
        return (node != null);
    }

    /**
     * Locate T in the tree.
     *
     * @param tree
     *            to search.
     * @param value
     *            to search for.
     * @return KdNode or NULL if not found
     */
    private static final <T extends ZipCodeLatLongDatabase.ZipCodeInfo> KdNode getNode(ZipCodeLatLongDatabase<T> tree, T value) {
        if (tree == null || tree.root == null || value == null)
            return null;

        KdNode node = tree.root;
        while (true) {
            if (node.id.equals(value)) {
                return node;
            } else if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    return null;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    return null;
                }
                node = node.greater;
            }
        }
    }

    /**
     * Remove first occurrence of value in the tree.
     *
     * @param value
     *            T to remove from the tree.
     * @return True if value was removed from the tree.
     */
    public boolean remove(T value) {
        if (value == null)
            return false;

        KdNode node = getNode(this, value);
        if (node == null)
            return false;

        KdNode parent = node.parent;
        if (parent != null) {
            if (parent.lesser != null && node.equals(parent.lesser)) {
                List<ZipCodeInfo> nodes = getTree(node);
                if (nodes.size() > 0) {
                    parent.lesser = createNode(nodes, node.k, node.depth);
                    if (parent.lesser != null) {
                        parent.lesser.parent = parent;
                    }
                } else {
                    parent.lesser = null;
                }
            } else {
                List<ZipCodeInfo> nodes = getTree(node);
                if (nodes.size() > 0) {
                    parent.greater = createNode(nodes, node.k, node.depth);
                    if (parent.greater != null) {
                        parent.greater.parent = parent;
                    }
                } else {
                    parent.greater = null;
                }
            }
        } else {
            // root
            List<ZipCodeInfo> nodes = getTree(node);
            if (nodes.size() > 0)
                root = createNode(nodes, node.k, node.depth);
            else
                root = null;
        }

        return true;
    }

    /**
     * Get the (sub) tree rooted at root.
     *
     * @param root
     *            of tree to get nodes for.
     * @return points in (sub) tree, not including root.
     */
    private static final List<ZipCodeInfo> getTree(KdNode root) {
        List<ZipCodeInfo> list = new ArrayList<ZipCodeInfo>();
        if (root == null)
            return list;

        if (root.lesser != null) {
            list.add(root.lesser.id);
            list.addAll(getTree(root.lesser));
        }
        if (root.greater != null) {
            list.add(root.greater.id);
            list.addAll(getTree(root.greater));
        }

        return list;
    }

    /**
     * K Nearest Neighbor search
     *
     * @param K
     *            Number of neighbors to retrieve. Can return more than K, if
     *            last nodes are equal distances.
     * @param value
     *            to find neighbors of.
     * @return unmodifiable collection of T neighbors.
     */
    @SuppressWarnings("unchecked")
    public Collection<T> nearestNeighbourSearch(int K, T value) {
        if (value == null)
            return null;

        // Map used for results
        TreeSet<KdNode> results = new TreeSet<KdNode>(new HaversineComparator(value));

        // Find the closest leaf node
        KdNode prev = null;
        KdNode node = root;
        while (node != null) {
            if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                prev = node;
                node = node.lesser;
            } else {
                // Greater
                prev = node;
                node = node.greater;
            }
        }
        KdNode leaf = prev;

        if (leaf != null) {
            // Used to not re-examine nodes
            Set<KdNode> examined = new HashSet<KdNode>();

            // Go up the tree, looking for better solutions
            node = leaf;
            while (node != null) {
                // Search node
                searchNode(value, node, K, results, examined);
                node = node.parent;
            }
        }

        // Load up the collection of the results
        Collection<T> collection = new ArrayList<T>(K);
        for (KdNode kdNode : results)
            collection.add((T) kdNode.id);
        return Collections.unmodifiableCollection(collection);
    }

    private static final <T extends ZipCodeLatLongDatabase.ZipCodeInfo> void searchNode(T value, KdNode node, int K,
                                                                     TreeSet<KdNode> results, Set<KdNode> examined) {
        examined.add(node);

        // Search node
        KdNode lastNode = null;
        Double lastDistance = Double.MAX_VALUE;
        if (results.size() > 0) {
            lastNode = results.last();
            lastDistance = lastNode.id.haversineDistance(value);
        }
        Double nodeDistance = node.id.haversineDistance(value);
        if (nodeDistance.compareTo(lastDistance) < 0) {
            if (results.size() == K && lastNode != null)
                results.remove(lastNode);
            results.add(node);
        } else if (nodeDistance.equals(lastDistance)) {
            results.add(node);
        } else if (results.size() < K) {
            results.add(node);
        }
        lastNode = results.last();
        lastDistance = lastNode.id.haversineDistance(value);

        int axis = node.depth % node.k;
        KdNode lesser = node.lesser;
        KdNode greater = node.greater;

        // Search children branches, if axis aligned distance is less than
        // current distance
        if (lesser != null && !examined.contains(lesser)) {
            examined.add(lesser);

            double nodePoint = Double.MIN_VALUE;
            double valuePlusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodePoint = node.id.latitude;
                valuePlusDistance = value.getLatitude() - lastDistance;
            } else if (axis == Y_AXIS) {
                nodePoint = node.id.longitude;
                valuePlusDistance = value.getLongitude() - lastDistance;
            }

            boolean lineIntersectsCube = ((valuePlusDistance <= nodePoint) ? true : false);

            // Continue down lesser branch
            if (lineIntersectsCube)
                searchNode(value, lesser, K, results, examined);
        }
        if (greater != null && !examined.contains(greater)) {
            examined.add(greater);

            double nodePoint = Double.MIN_VALUE;
            double valuePlusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodePoint = node.id.latitude;
                valuePlusDistance = value.getLatitude() + lastDistance;
            } else if (axis == Y_AXIS) {
                nodePoint = node.id.longitude;
                valuePlusDistance = value.getLongitude() + lastDistance;
            }

            boolean lineIntersectsCube = ((valuePlusDistance >= nodePoint) ? true : false);

            // Continue down greater branch
            if (lineIntersectsCube)
                searchNode(value, greater, K, results, examined);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }

    protected static class HaversineComparator implements Comparator<KdNode> {

        private ZipCodeInfo point = null;

        public HaversineComparator(ZipCodeInfo point) {
            this.point = point;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(KdNode o1, KdNode o2) {
            Double d1 = point.haversineDistance(o1.id);
            Double d2 = point.haversineDistance(o2.id);
            int comparisonResult = d1.compareTo(d2);
            if(comparisonResult < 0)
                return -1;
            else if (comparisonResult > 0)
                return 1;
            return o1.id.compareTo(o2.id);
        }
    };

    public static class KdNode implements Comparable<KdNode>
    {
        private int k = 3;
        private int depth = 0;
        private ZipCodeInfo id = null;
        private KdNode parent = null;
        private KdNode lesser = null;
        private KdNode greater = null;

        public KdNode(ZipCodeInfo id)
        {
            this.id = id;
        }

        public KdNode(int k, int depth, ZipCodeInfo id)
        {
            this(id);
            this.k = k;
            this.depth = depth;
        }

        public static int compareTo(int depth, int k, ZipCodeInfo o1, ZipCodeInfo o2)
        {
            int axis = depth % k;
            if (axis == X_AXIS)
                return LATITUDE_COMPARATOR.compare(o1, o2);
            if (axis == Y_AXIS)
                return LONGITUDE_COMPARATOR.compare(o1, o2);
            return LATITUDE_COMPARATOR.compare(o1, o2);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return 31 * (this.k + this.depth + this.id.hashCode());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;
            if (!(obj instanceof KdNode))
                return false;

            KdNode kdNode = (KdNode) obj;
            if (this.compareTo(kdNode) == 0)
                return true;
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(KdNode o)
        {
            return compareTo(depth, k, this.id, o.id);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("k=").append(k);
            builder.append(" depth=").append(depth);
            builder.append(" id=").append(id.toString());
            return builder.toString();
        }
    }

    public static class ZipCodeInfo implements Comparable<ZipCodeInfo>
    {
        @Getter
        private double latitude = Double.NEGATIVE_INFINITY;
        @Getter
        private double longitude = Double.NEGATIVE_INFINITY;
        @Getter
        private Set<String> zipCodeSet;
        @Getter
        private String countryCode;
        /*accuracy of lat/lng from 1=estimated to 6=centroid*/
        @Getter
        private short accuracy;

        public ZipCodeInfo(double latitude,
                           double longitude,
                           Set<String> zipCodeSet,
                           String countryCode,
                           short accuracy)
        {
            this.latitude = latitude;
            this.longitude = longitude;
            this.zipCodeSet = zipCodeSet;
            this.countryCode = countryCode;
            this.accuracy = accuracy;
        }


        /**
         * Computes the Haversine distance from this point to the other.
         *
         * @param o1
         *            other point.
         */
        public double haversineDistance(ZipCodeInfo o1)
        {
            return haversineDistance(o1, this);
        }

        private static final double haversineDistance(ZipCodeInfo o1,ZipCodeInfo o2)
        {
            Double latDistance = toRad(o2.latitude-o1.latitude);
            Double lonDistance = toRad(o2.longitude-o1.longitude);
            Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                       Math.cos(toRad(o1.latitude)) * Math.cos(toRad(o2.latitude)) *
                       Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return EARTH_RADIUS_KMS * c;
        }

        private static double toRad(double value)
        {
            return value * Math.PI / 180;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return 31 * (int)(this.latitude + this.longitude);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;
            if (!(obj instanceof ZipCodeInfo))
                return false;

            ZipCodeInfo zipCodeInfo = (ZipCodeInfo) obj;
            return compareTo(zipCodeInfo) == 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(ZipCodeInfo o)
        {
            int xComp = LATITUDE_COMPARATOR.compare(this, o);
            if (xComp != 0)
                return xComp;
            int yComp = LONGITUDE_COMPARATOR.compare(this, o);
            if (yComp != 0)
                return yComp;
           return xComp;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            builder.append(latitude).append(", ");
            builder.append(longitude);
            builder.append(")");
            return builder.toString();
        }
    }

    protected static class TreePrinter
    {
        public static <T extends ZipCodeInfo> String getString(ZipCodeLatLongDatabase<T> tree)
        {
            if (tree.root == null)
                return "Tree has no nodes.";
            return getString(tree.root, "", true);
        }

        private static String getString(KdNode node, String prefix, boolean isTail) {
            StringBuilder builder = new StringBuilder();

            if (node.parent != null) {
                String side = "left";
                if (node.parent.greater != null && node.id.equals(node.parent.greater.id))
                    side = "right";
                builder.append(prefix + (isTail ? "└── " : "├── ") + "[" + side + "] " + "depth=" + node.depth + " id="
                        + node.id + "\n");
            } else {
                builder.append(prefix + (isTail ? "└── " : "├── ") + "depth=" + node.depth + " id=" + node.id + "\n");
            }
            List<KdNode> children = null;
            if (node.lesser != null || node.greater != null) {
                children = new ArrayList<KdNode>(2);
                if (node.lesser != null)
                    children.add(node.lesser);
                if (node.greater != null)
                    children.add(node.greater);
            }
            if (children != null) {
                for (int i = 0; i < children.size() - 1; i++) {
                    builder.append(getString(children.get(i), prefix + (isTail ? "    " : "│   "), false));
                }
                if (children.size() >= 1) {
                    builder.append(getString(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "),
                            true));
                }
            }

            return builder.toString();
        }
    }
}