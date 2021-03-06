package albumsimplecore.a_owner_remove_groups_finefit.it.unito.Album;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/*** added by DBaseFineFit
 */
import com.finefit.sut.State;
import com.finefit.sut.IdMap;

/*** added by DBase* modified by DRemove* modified by DOwner* modified by
DRemoveAndOwner* modified by DGroups
 */
public class ArrayPhotoAlbum implements PhotoAlbum {
	private int size = 0;
	private Photo [] photoAt;
	boolean imageIsInAlbum(String image) {
		for(int i = 0;
			i < size;
			i ++) {
			Photo p = photoAt[i];
			if(p.getImage().equals(image)) return true;
		}
		return false;
	}
	/*** modified by DOwner* modified by DGroups
	 */
	public Photo addPhoto(String image) {
		Photo p = addPhoto_original4(image);
		p.setGroup(groups.get(OWNER_GROUP_NAME));
		return p;
	}
	/*** modified by DGroups
	 */
	public Set<Photo> viewPhotos() {
		if(loggedUser == null) throw new NotAuthorized();
		Set<Photo> result = new HashSet<Photo>();
		for(int i = 0;
			i < size;
			i ++) {
			if(photoAt[i].getGroup().getMembers().contains(loggedUser)) {
				result.add(photoAt[i]);
			}
		}
		return result;
	}
	/*** added by DRemove* modified by DRemoveAndOwner
	 */
	public void removePhoto(int location) {
		if(! isOwnerLoggedIn()) throw new OwnerNotLoggedIn();
		removePhoto_original2(location);
	}
	/*** added by DOwner
	 */
	private User owner;
	/*** added by DOwner
	 */
	private User loggedUser;
	/*** added by DOwner
	 */
	private Map<String, User> users;
	/*** added by DOwner
	 */
	private boolean isOwnerLoggedIn() {
		return loggedUser == owner;
	}
	/*** added by DOwner
	 */
	public void login(String name, String password) {
		if(loggedUser != null) throw new AlreadyLogged();
		if((! users.containsKey(name)) ||(!
				users.get(name).getPassword().equals(password))) throw new AuthFailed();
		loggedUser = users.get(name);
	}
	/*** added by DOwner
	 */
	public void logout() {
		loggedUser = null;
	}
	/*** modified by DOwner
	 */
	public Photo addPhoto_original0(String image) {
		if(image == null) throw new IllegalArgumentException("NullImage");
		if(size == photoAt.length) throw new AlbumIsFull();
		if(imageIsInAlbum(image)) throw new PhotoExists();
		Photo new_photo = new Photo(image);
		photoAt[size] = new_photo;
		size = size + 1;
		return new_photo;
	}
	/*** added by DRemove* modified by DRemoveAndOwner
	 */
	public void removePhoto_original2(int location) {
		if((location < 0) ||(size <= location)) throw new
		IllegalArgumentException("IllegalLocation");
		photoAt[location] = photoAt[size - 1];
		photoAt[size - 1] = null;
		size = size - 1;
	}

  /*** added by DOwner_FineFit modified by DGroups_FineFit
  */
  public ArrayPhotoAlbum(int maxSize) {
    if (maxSize < 1)
      throw new IllegalArgumentException("IllegalSize");
    photoAt = new Photo[maxSize];
    users = new HashMap<String,User>();
    this.owner = null;

/*** added by DGroups_FineFit
*/
    groups = new HashMap<String,Group>();
  }

/*** added by DGroups_FineFit
*/
  Group ownerGroup;

  public void setOwnerGroup(Group ownerGroup) {
    groups.put(ownerGroup.getName(), ownerGroup);
    this.ownerGroup = ownerGroup;
  }


  /*** added by DOwnerFineFit
  */
  public void setOwner(User owner) {
    this.owner = owner;
    users.put(owner.getName(), owner);
  }

	/*** added by DGroups
	 */
	private Map<String, Group> groups;
	/*** added by DGroups
	 */
	private static final String OWNER_GROUP_NAME = "Owner";
	/*** added by DGroups
	 */
	public ArrayPhotoAlbum(int maxSize, String ownerName, String ownerPwd) {
		if(maxSize < 1) throw new IllegalArgumentException("IllegalSize");
		photoAt = new Photo[maxSize];
		owner = new User(ownerName, ownerPwd);
		users = new HashMap<String, User>();
		users.put(ownerName, owner);
		groups = new HashMap<String, Group>();
		groups.put(OWNER_GROUP_NAME, new Group(OWNER_GROUP_NAME, owner));
	}
	/*** added by DGroups
	 */
	public User updateUser(String name, String password) {
		if((name == null) ||(password == null)) throw new
		IllegalArgumentException();
		if(! isOwnerLoggedIn()) throw new NotAuthorized();
		if(users.containsKey(name)) {
			users.get(name).setPassword(password);
			return null;
		}
		else {
			User user = new User(name, password);
			users.put(name, user);
			return user;
		}
	}
	/*** added by DGroups
	 */
	public Group updateGroup(String name, Set<String> memberNames) {
		if((name == null) ||(memberNames == null)) throw new
		IllegalArgumentException();
		if(! isOwnerLoggedIn()) throw new NotAuthorized();
		if(! users.keySet().containsAll(memberNames)) throw new MissingUsers();
		Set<User> members = new HashSet<User>();
		for(String n : memberNames) {
			members.add(users.get(n));
		}
		if(groups.containsKey(name)) {
			groups.get(name).setMembers(members);
			return null;
		}
		else {
			Group new_group = new Group(name, members);
			groups.put(name, new_group);
			return new_group;
		}
	}
	/*** added by DGroups
	 */
	public void removeUser(String userName) {
		if(userName == null) throw new IllegalArgumentException();
		if(! isOwnerLoggedIn()) throw new NotAuthorized();
		if(! users.keySet().contains(userName)) throw new MissingUser();
		if(userName.equals(owner.getName())) throw new RemoveOwner();
		User user = users.get(userName);
		for(Group g : groups.values()) {
			g.getMembers().remove(user);
		}
		users.remove(userName);
	}
	/*** added by DGroups
	 */
	public void removeGroup(String name) {
		if(! isOwnerLoggedIn()) throw new NotAuthorized();
		if(name.equals(OWNER_GROUP_NAME)) throw new RemoveOwnerGroup();
		if((name == null) ||(! groups.containsKey(name))) throw new MissingGroup();
		boolean hasPhoto = false;
		int i = 0;
		while((! hasPhoto) &&(i < size)) {
			hasPhoto =(photoAt[i].getGroup() == groups.get(name));
			i ++;
		}
		if(hasPhoto) throw new IllegalArgumentException("GroupHasPhoto" +(i - 1));
		groups.remove(name);
	}
	/*** added by DGroups
	 */
	public void updatePhotoGroup(int location, String groupName) {
		if(! isOwnerLoggedIn()) throw new OwnerNotLoggedIn();
		if((location < 0) ||(size <= location)) throw new
		IllegalArgumentException("IllegalLocation");
		if(! groups.containsKey(groupName)) throw new MissingGroup();
		photoAt[location].setGroup(groups.get(groupName));
	}
	/*** modified by DOwner* modified by DGroups
	 */
	public Photo addPhoto_original4(String image) {
		if(! isOwnerLoggedIn()) throw new OwnerNotLoggedIn();
		return addPhoto_original0(image);
	}
  /*** added by DBaseFineFit modified by DOwnerFineFit modified by DGropus_FineFit
  */
  public State retrieve() {
    State state = new State();

    state.add_state("photoAt", 2);

    for (int i = 0; i < size; i++) {
      state.get_state("photoAt").add("" + i, IdMap.instance().obj2atom(photoAt[i]));
    }

    state.add_state("ownerName", 1).add(owner.getName());

    state.add_state("users", 2);
    for(Map.Entry<String, User> e : users.entrySet()) {
      state.get_state("users").add(e.getKey(), IdMap.instance().obj2atom(e.getValue()));
    }

    state.add_state("loggedIn", 1);

    if (loggedUser != null)
      state.get_state("loggedIn").add(loggedUser.getName());

    state.add_state("ownerGroupName", 1).add(ownerGroup.getName());

    state.add_state("groups", 2);
    for(Map.Entry<String, Group> e : groups.entrySet()) {
      state.get_state("groups").add(e.getKey(), IdMap.instance().obj2atom(e.getValue()));
    }

    state.add_state("groupPhotos", 2);
    int i = 0;
    while(i < size) {
      state.get_state("groupPhotos").add(IdMap.instance().obj2atom(photoAt[i]), IdMap.instance().obj2atom(photoAt[i].getGroup()));
      ++i;
    }

    state.add_state("members", 2);
    for(Group g : groups.values()) {
      for(User u : g.getMembers()) {
        state.get_state("members").add(IdMap.instance().obj2atom(g), IdMap.instance().obj2atom(u));
      }
    }

        state.add_state("passwords", 2);

        for(User u : users.values()) {
            state.get_state("passwords").add(IdMap.instance().obj2atom(u), u.getPassword());
        }

    return state;
  }

}
