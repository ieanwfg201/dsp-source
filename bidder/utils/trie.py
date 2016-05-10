class TrieNode :
    def __init__(self, value = None) :
        """ Constructor for the node 

            Keyword arguments :
            value -- value in this node
        """
        self.value = value
        self.childrenMap = {}

    def insertChild(self, childKey, childNode, overWrite = False) :
        """ Insert a child corresponding to child key 

            Keyword arguments :
            childKey -- key for the given child
            childNode -- Child node for the key
            overWrite -- Specifies whether the child should be overwritten if it already exists. If false and the child already exists, raise an exception
        """
        if not overWrite and self.childrenMap.get(childKey) != None :
            raise Exception("Child corresponding to key " + childKey + " already exists")

        self.childrenMap[childKey] = childNode

    def insertChildForList(self, childKeyList, valueInLeaf) :
        """ Insert a child having the value 'valueInLeaf' corresponding to the given key list

            Keyword arguments :
            childKeyList -- List having the keys for the path
            valueInLeaf -- value to be put in the leaf node inserted
        """
        self.insertChildForListRecursive(childKeyList, valueInLeaf, 0)

    def insertChildForListRecursive(self, childKeyList, valueInLeaf, position) :
        """ Insert a child having the value 'valueInLeaf' corresponding to the given key list, starting from the position 'position' in childKeyList

            Keyword arguments :
            childKeyList -- List having the keys for the path
            valueInLeaf -- value to be put in the leaf node inserted
            position -- position in the list from where the insertion starts
        """
        if childKeyList is None or len(childKeyList) == 0 :
            return
        childKey = childKeyList[position]
        childNode = self.childrenMap.get(childKey)
        if childNode is None :
            # If no child node present, insert a new child node corresponding to the key at this location
            childNode = TrieNode()
            self.childrenMap[childKey] = childNode

        if position == len(childKeyList) - 1 :
            childNode.value = valueInLeaf
        else :
            childNode.insertChildForListRecursive(childKeyList, valueInLeaf, position + 1)

    def getChild(self, childKey) :
        """ Get child corresponding to the given key

            Keyword arguments :
            childKey -- Key for which child is to be returned

            return Returns the child node if present, else return None
        """
        return self.childrenMap.get(childKey)

    def getChildMap(self) :
        """ Returns the whole child map

            return Returns the key -> child node map
        """
        return self.childrenMap

    def getChildValue(self, childKey) :
        """ Get the value stored in the child corresponding to the given key

            Keyword arguments :
            childKey -- Key for which child is to be returned

            return Returns value in the child node if present, else return None
        """
        childNode = self.childrenMap.get(childKey)
        if childNode is None :
            return None
        return childNode.value

    def getChildForList(self, childKeyList) :
        """ Get child corresponding to the key list

            Keyword arguments :
            childKeyList -- List of keys for which the child is to be returned.

            return Returns the child node if present, else return None
        """
        return self.getChildRecursive(childKeyList, 0)

    def getChildValueForList(self, childKeyList) :
        """ Get value in the child corresponding to the key list

            Keyword arguments :
            @type childKeyList: list
            childKeyList -- List of keys for which the child is to be returned.

            return Returns the value in child node if present, else return None
        """
        childNode = self.getChildRecursive(childKeyList, 0)
        if childNode is None :
            return None
        return childNode.value

    def getChildRecursive(self, childKeyList, position) :
        """ Get child for the key list starting at the given position in the list

            Keyword arguments :
            childKeyList -- List of keys for which the child is to be returned.
            position -- Position in the list starting from where the child is to be found

            return Returns the child node if present, else return None
        """
        if childKeyList == None or len(childKeyList) == 0 :
            return self
        childKey = childKeyList[position]
        childNode = self.childrenMap.get(childKey)
        if childNode is None :
            return None

        if position == len(childKeyList) - 1 :
            return childNode

        return childNode.getChildRecursive(childKeyList, position + 1)


if __name__ == '__main__' :
    # Tests for the node
    root = TrieNode()
    childKeyList = [3, 2, 5, 10]
    childKeyList2 = [3, 2, 5, 10, 11]
    childKeyList3 = [3, 2, 5, 9]
    childKeyList4 = [4, 7, 6]
    root.insertChildForListRecursive(childKeyList, 100, 0)
    root.insertChildForListRecursive(childKeyList2, 200, 0)
    root.insertChildForListRecursive(childKeyList3, 300, 0)
    root.insertChildForListRecursive(childKeyList4, 400, 0)
    value = root.getChildValueForList(childKeyList)
    print value
    value = root.getChildValueForList(childKeyList2)
    print value
    value = root.getChildValueForList(childKeyList3)
    print value
    value = root.getChildValueForList(childKeyList4)
    print value
    value = root.getChildValueForList([4, 7, 8])
    print value
    root.insertChildForListRecursive(childKeyList, 700, 0)
    value = root.getChildValueForList(childKeyList)
    print value
    childNode = root.getChild(4)
    print childNode
    childNode = root.getChild(100)
    print childNode
