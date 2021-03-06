/* Copyright 2014 David Faitelson

This file is part of FineFit.

FineFit is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FineFit is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FineFit. If not, see <http://www.gnu.org/licenses/>.

*/

package albumsimplecore.a_remove_owner_finefit.it.unito.Album;

import java.util.Set;
import java.util.HashSet;
import com.finefit.sut.*;
import com.finefit.sut.FineFitDriver;

public class PhotoAlbumDriver extends FineFitDriver {

		protected void setup_exception_table() {

      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$AlbumIsFull", "ALBUM_FULL");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$OwnerNotLoggedIn", "NOT_AUTH");

			// DRemoveFineFit

			exceptions.put("java.lang.IllegalArgumentException", "NO_PHOTO");

			// DOwnerFineFit

      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$AlreadyLogged", "ALREADY_IN");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$AuthFailed", "AUTH_FAILED");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$PhotoExists", "PHOTO_EXISTS");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$MissingGroup", "NO_GROUP");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$RemoveOwnerGroup", "REM_OWNER_GROUP");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$NotAuthorized", "NOT_AUTH");
      exceptions.put("albumsimplecore.a_remove_owner_finefit.it.unito.Album.PhotoAlbum$MissingUsers", "MISSING_USERS");
		}

    protected void setup_operation_table() {


			ops.put("addPhoto", new Operation() { 
				PhotoAlbum s = sut;
				public void apply(com.finefit.model.State args, State outputs) throws Exception {
					String id = args.getArg("p?");
					Photo p = sut.addPhoto(id); 
					IdMap.instance().associate(p, id);
				} });

			ops.put("viewPhotos", new Operation() { 
				PhotoAlbum s = sut;
				public void apply(com.finefit.model.State args, State outputs) throws Exception {

					outputs.add_output("result!", 1);

					Set<Photo> photos = sut.viewPhotos(); 
					for(Photo p : photos){
						outputs.get_output("result!").add(IdMap.instance().obj2atom(p));
					}
				 } });

			/*** added by DOwner_FineFit
			*/
			ops.put("login", new Operation() { 
				PhotoAlbum s = sut;
				public void apply(com.finefit.model.State args, State outputs) throws Exception {
					s.login(args.getArg("n?"), args.getArg("p?")); } });

      /*** added by DRemoveFineFit
      */
      ops.put("removePhoto", new Operation() {
        PhotoAlbum s = sut;
        public void apply(com.finefit.model.State args, State outputs) throws Exception {
          int i = Integer.parseInt(args.getArg("l?"));
          sut.removePhoto(i);
        } });

		}

		private ArrayPhotoAlbum sut;

		@Override protected void setup_sut() {
			sut = new ArrayPhotoAlbum(5);
		}

		@Override public State retrieve() { return sut.retrieve(); }

		/*** modified by DOwner_FineFit
		*/

		@Override public void init_sut(com.finefit.model.State args) {

			String owner_name = args.getArg("n?");
			String owner_passwd = args.getArg("p?");
			User owner = new User(owner_name, owner_passwd);

			sut.setOwner(owner);

		}
}

