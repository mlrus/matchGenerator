/**
 * 
 */
package bayesMatchGenerator;

interface Accessor<CONTAINER, ELEMENT> {
	public ELEMENT getKey(CONTAINER base);
}

interface Accessible<CONTAINER, ELEMENT> {
	Accessor<CONTAINER, ELEMENT> getAccessor();
}