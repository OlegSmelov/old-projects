require 'spec_helper'

describe SessionsController do
  describe '#new' do
    it "renders :new template" do
      get :new
      expect(response).to render_template :new
    end
  end

  describe '#create' do
    let!(:user) do
      create(:user, name: "john", password: "doe", password_confirmation: "doe")
    end

    it "sets session data after authentication" do
      post :create, session: { name: "john", password: "doe" }
      expect(session[:user_id]).to eq(user.id)
    end

    it "redirects to user's profile" do
      post :create, session: { name: "john", password: "doe" }
      expect(response).to redirect_to user
    end

    context "when login data is wrong" do
      before :each do
        post :create, session: { name: "john", password: "123" }
      end

      it "flashes error message" do
        expect(flash[:error]).to_not be_empty
      end

      it "renders :new template" do
        expect(response).to render_template :new
      end

      it "does not set session data" do
        expect(session[:user_id]).to_not eq(user.id)
      end
    end
  end

  describe '#destroy' do
    context "when logged in" do
      let(:user) { create(:user, name: "john", password: "doe", password_confirmation: "doe") }

      before :each do
        session[:user_id] = user.id
      end

      it "logs you out" do
        expect {
          delete :destroy
        }.to change {
          session[:user_id]
        }.from(user.id).to(nil)
      end

      it "redirects you to root_url" do
        delete :destroy
        expect(response).to redirect_to root_url
      end
    end
  end
end
