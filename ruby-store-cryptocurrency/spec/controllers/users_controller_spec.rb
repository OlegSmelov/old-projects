require 'spec_helper'

describe UsersController do
  describe '#index' do
    it "populates an array of users" do
      user = create(:user)
      get :index
      expect(assigns(:users)).to eq([user])
    end

    it "renders :index view" do
      get :index
      expect(response).to render_template :index
    end
  end

  describe '#new' do
    it "creates a new user object" do
      get :new
      expect(assigns(:user)).to be_instance_of User
    end

    it "renders :new view" do
      get :new
      expect(response).to render_template :new
    end
  end

  describe '#create' do
    it "creates a new user" do
      expect {
        post :create, user: attributes_for(:user)
      }.to change {
        User.count
      }.by(1)
    end

    it "redirects to root_url after creating" do
      post :create, user: attributes_for(:user)
      expect(response).to redirect_to root_url
    end

    it "sets a success flash message" do
      post :create, user: attributes_for(:user)
      expect(flash[:success]).to_not be_empty
    end

    context "with invalid attributes" do
      let(:invalid_attributes) do
        attributes = attributes_for(:user)
        attributes[:name] = ''
        attributes
      end

      it "renders the :new template" do
        post :create, user: invalid_attributes
        expect(response).to render_template :new
      end

      it "does not save the user" do
        expect {
          post :create, user: invalid_attributes
        }.to_not change { User.count }
      end
    end
  end

  describe '#show' do
    it "loads user by id" do
      user = create(:user)
      get :show, id: user.id
      expect(assigns(:user)).to eq(user)
    end

    it "assigns nil to @user if user not found" do
      get :show, id: 12345
      expect(assigns(:user)).to be_nil
    end

    it "renders :show template" do
      user = create(:user)
      get :show, id: user.id
      expect(response).to render_template :show
    end
  end

  describe '#update' do
    context "when not logged in" do
      it "doesn't update user data" do
        user = create(:user)
        expect {
          post :update, id: user.id, user: { name: "newname" }
        }.to_not change {
          user.reload.name
        }
      end

      it "redirects to sign in url" do
        user = create(:user)
        post :update, id: user.id, user: { name: "newname" }
        expect(response).to redirect_to signin_url
      end
    end

    context "when logged in" do
      let(:user) { create(:user, name: "oldname") }
      before :each do
        session[:user_id] = user.id
      end

      it "updates your user" do
        post :update, id: user.id, user: { name: "newname" }
        expect(user.reload.name).to eq("newname")
      end

      it "redirects to user's profile" do
        post :update, id: user.id, user: { name: "newname" }
        expect(response).to redirect_to user
      end

      it "renders :edit template on error" do
        post :update, id: user.id, user: { name: "" }
        expect(response).to render_template :edit
      end
    end
  end
end