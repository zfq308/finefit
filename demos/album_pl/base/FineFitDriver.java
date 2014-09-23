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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import com.finefit.sut.SUT;
import com.finefit.sut.NoSuchOperation;
import com.finefit.sut.InvalidNumberOfArguments;
import com.finefit.sut.Operation;
import com.finefit.sut.State;
import com.finefit.sut.IdMap;
import com.finefit.model.TestCase;

public class FineFitDriver implements SUT {

    private static Map<String, Operation<PhotoAlbum> > ops = new HashMap<String, Operation<PhotoAlbum> >();

    static {

      ops.put("addPhoto", new Operation<PhotoAlbum>() {
        public void apply(PhotoAlbum sut, com.finefit.model.State args, State outputs) throws Exception {
          String id = args.getArg("p");
          Photo p = sut.addPhoto(id);
          IdMap.instance().associate(p, id);
        } });

      ops.put("viewPhotos", new Operation<PhotoAlbum>() {
        public void apply(PhotoAlbum sut, com.finefit.model.State args, State outputs) throws Exception {

          outputs.add_output("result!", 1);

          Set<Photo> photos = sut.viewPhotos();
          
          for(Photo p : photos) {
						outputs.get_output("result!").add(IdMap.instance().obj2atom(p));
          }
				} });
		}

		private ArrayPhotoAlbum sut;
		
    public FineFitDriver() {
			sut = new ArrayPhotoAlbum(5);
    }

    @Override
    public State initialize(com.finefit.model.State args) {
			sut = new ArrayPhotoAlbum(5);
      return sut.retrieve();
    }

    @Override
    public State applyOperation(TestCase testCase) throws Exception {

      String operationName = testCase.getOperationName();
      com.finefit.model.State args = testCase.getState();

      Operation<PhotoAlbum> op = ops.get(operationName);
      if (op == null)
        throw new NoSuchOperation(operationName);

      String report = "OK$0";
      State outputs = new State();

      try {
        op.apply(sut, args, outputs);
      }
      catch(PhotoAlbum.PhotoExists err) { report = "PHOTO_EXISTS$0"; }
      catch(PhotoAlbum.AlbumIsFull err) { report = "ALBUM_FULL$0"; }

      outputs.add_output("report!", 1).add(report);

			State state = sut.retrieve();
			state.add(outputs);
			return state;
    }
}

